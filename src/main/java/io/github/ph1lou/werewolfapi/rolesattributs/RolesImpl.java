package io.github.ph1lou.werewolfapi.rolesattributs;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.*;
import io.github.ph1lou.werewolfapi.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public abstract class RolesImpl implements Roles, Listener,Cloneable {
    
    protected final WereWolfAPI game;
    protected final GetWereWolfAPI main;

    @NotNull
    private UUID uuid;
    private boolean infected = false;
    private boolean transformedToNeutral = false;
    private boolean solitary =false;
    @NotNull
    private PlayerWW playerWW;
    @NotNull
    private final String key;
    private String deathRole="";

    public RolesImpl(@NotNull GetWereWolfAPI main,@NotNull PlayerWW playerWW ,@NotNull String key){
        this.main = main;
        this.game = main.getWereWolfAPI();
        this.uuid=playerWW.getUUID();
        this.playerWW=playerWW;
        this.key=key;
    }

    @Override
    public @NotNull String getDescription() {
        return game.translate("werewolf.description.role", game.translate(this.getDeathRole())) + (!this.getDeathRole().equals(this.getKey())?game.translate("werewolf.role.thief.thief",this.getKey()):"") +
                (this.infected ? game.translate("werewolf.end.infect"):"") + (this.solitary?game.translate("werewolf.end.solitary"):"") + '\n' +
                game.translate("werewolf.description.camp", getCamp().getChatColor()+game.translate(getCamp().getKey())) +
                (isWereWolf()? game.translate("werewolf.description.werewolf"):"") +
                (this instanceof Transformed ? game.translate("werewolf.description.transformed",game.translate(((Transformed)this).getTransformed()?"werewolf.description.yes":"werewolf.description.no")):"");
    }

    @Override
    public boolean isNeutral() {
        return solitary || transformedToNeutral && !this.infected;
    }

    @Override
    public final boolean isTransformedToNeutral() {
        return transformedToNeutral;
    }

    @Override
    public final void setTransformedToNeutral(boolean neutral) {
        transformedToNeutral =neutral;
    }

    @Override
    public final @NotNull String getKey() {
        return key;
    }

    @NotNull
    @Override
    public final UUID getPlayerUUID(){
        return uuid;
    }

    @Override
    public final boolean isKey(@NotNull String s) {
        return s.equals(this.getKey());
    }

    @Override
    public final @NotNull Camp getCamp() {

        if(isNeutral()){
            return Camp.NEUTRAL;
        }

        if(isWereWolf()){
            return Camp.WEREWOLF;
        }
        return Camp.VILLAGER;
    }

    @Override
    public final boolean isCamp(@NotNull Camp camp) {
        return camp.equals(getCamp());
    }

    @Override
    public final Roles publicClone() {
        try {
            return (Roles) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @EventHandler
    public void onModeratorScoreBoard(UpdateModeratorNameTag event){

        StringBuilder sb = new StringBuilder(event.getPrefix());

        if(!uuid.equals(event.getPlayerUUID())) return;

        if(playerWW.isState(StatePlayer.DEATH)) return;

        if(isNeutral()){
            sb.append(ChatColor.GOLD);
        }
        else if(isWereWolf()){
            sb.append(ChatColor.DARK_RED);
        }
        else sb.append(ChatColor.GREEN);

        event.setPrefix(sb.toString());
        event.setSuffix(sb.toString());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRequestWereWolfList(RequestSeeWereWolfListEvent event){

        if(!uuid.equals(event.getPlayerUUID())) return;

        if(playerWW.isState(StatePlayer.DEATH)) return;

        if (game.getConfig().getTimerValue(TimersBase.WEREWOLF_LIST.getKey()) <= 0) {
            event.setAccept(isWereWolf());
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAppearInWereWolfList(AppearInWereWolfListEvent event){

        if(!uuid.equals(event.getPlayerUUID())) return;

        if(playerWW.isState(StatePlayer.DEATH)) return;

        event.setAppear(isWereWolf());
    }

    @EventHandler
    public void onWereWolfList(WereWolfListEvent event){

        if(!isWereWolf()) return;

        if(!playerWW.isState(StatePlayer.ALIVE)) return;

        playerWW.sendMessageWithKey("werewolf.role.werewolf.see_others");
        Sound.WOLF_HOWL.play(getPlayerWW());
    }

    @EventHandler
    public void onNewWereWolf(NewWereWolfEvent event) {

        if(!playerWW.equals(event.getPlayerWW())) return;
        if(getPlayerWW().isState(StatePlayer.DEATH)) return;

        playerWW.sendMessageWithKey("werewolf.role.werewolf.go_to_the_werewolf_camp");
        Sound.WOLF_HOWL.play(getPlayerWW());
        recoverPotionEffect();

        game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.getRole().isWereWolf())
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(playerWW -> Bukkit.getPlayer(playerWW.getUUID()))
                .filter(Objects::nonNull)
                .forEach(player1 -> {
                    player1.sendMessage(game.translate("werewolf.role.werewolf.new_werewolf"));
                    Sound.WOLF_HOWL.play(player1);
                });
    }

    @Override
    public boolean isWereWolf() {
        return infected;
    }

    @Override
    public final boolean getInfected() {
        return infected;
    }

    @Override
    public final void setInfected() {
        this.infected = true;
    }



    @EventHandler
    public void onEndPlayerMessageInfected(EndPlayerMessageEvent event){

        if(!playerWW.equals(event.getPlayerWW())) return;

        StringBuilder sb = event.getEndMessage();

        if(infected){
            sb.append(game.translate("werewolf.end.infect"));
        }

        if(solitary){
            sb.append(game.translate("werewolf.end.solitary"));
        }

    }

    @Override
    public void recoverPotionEffect() {

        if(!isWereWolf()) return;

        playerWW.addPotionEffect(PotionEffectType.NIGHT_VISION);
        if(game.isDay(Day.DAY)) return;
        playerWW.addPotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @EventHandler
    public void onWWChat(WereWolfChatEvent event){
        if(event.isCancelled()) return;

        if(!getPlayerWW().isState(StatePlayer.ALIVE)) return;
        if(!isWereWolf()) return;

        getPlayerWW().sendMessageWithKey("werewolf.commands.admin.ww_chat.prefix",event.getMessage());

    }

    @EventHandler
    public void onChatSpeak(WereWolfCanSpeakInChatEvent event){

        if(!playerWW.equals(event.getPlayerWW())) return;

        if(!playerWW.isState(StatePlayer.ALIVE)) return;

        if(!isWereWolf()) return;

        event.setCanSpeak(true);

    }


    @EventHandler
    public void onCountCategories(CountRemainingRolesCategoriesEvent event){

        if (!playerWW.isState(StatePlayer.ALIVE)) return;

        if(isNeutral()){
            event.addNeutral();
            return;
        }

        if(isWereWolf()){
            event.addWerewolf();
            return;
        }

        event.addVillager();
    }

    @EventHandler
    public void onPlayerDeathByWereWolf(PlayerDeathEvent event) {

        if(!isWereWolf()) return;

        if(!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if(event.getEntity().getKiller()==null) return;

        Player killer = event.getEntity().getKiller();

        if(!uuid.equals(killer.getUniqueId())) return;

        killer.removePotionEffect(PotionEffectType.ABSORPTION);
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0, false, false));
    }

    @Override
    public final String getBetterDescription(){
        return game.translate("werewolf.description._") +
                getDescription() +
                game.translate("werewolf.description._");
    }

    @Override
    public final void roleAnnouncement(){

        Sound.EXPLODE.play(getPlayerWW());
        getPlayerWW().sendMessage(getBetterDescription());
        getPlayerWW().sendMessageWithKey("werewolf.announcement.review_role");

        recoverPotionEffect();
        recoverPower();

        if(game.getConfig().isTrollSV()) return;

        if(!game.getStuffs().getStuffRoles().containsKey(getKey())){
            Bukkit.getConsoleSender().sendMessage("[WereWolfPlugin] invalid addon structure");
            return;
        }

        for(ItemStack i:game.getStuffs().getStuffRoles().get(getKey())) {
            playerWW.addItem(i);
        }
    }

    @Override
    public final @NotNull PlayerWW getPlayerWW() {
        return playerWW;
    }

    @Override
    public final void setPlayerWW(@NotNull PlayerWW playerWW) {
        this.playerWW=playerWW;
        this.uuid=playerWW.getUUID();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNightForWereWolf(NightEvent event) {

        if(!getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        if(!isWereWolf()) return;

        getPlayerWW().addPotionEffect(PotionEffectType.INCREASE_DAMAGE);

        if(!game.getConfig().isConfigActive(ConfigsBase.WEREWOLF_CHAT.getKey())) return;

        openWereWolfChat();

    }

    protected void openWereWolfChat(){
        getPlayerWW().sendMessageWithKey("werewolf.commands.admin.ww_chat.announce",
                game.getScore()
                        .conversion(
                                game.getConfig()
                                        .getTimerValue(
                                                TimersBase.WEREWOLF_CHAT_DURATION
                                                        .getKey())),
                game.getConfig().getWereWolfChatMaxMessage());

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main,
                () -> {
                    if(!game.isState(StateGame.END)){
                        getPlayerWW()
                                .sendMessageWithKey("werewolf.commands.admin.ww_chat.disable");
                    }
                },
                game.getConfig().getTimerValue(TimersBase.WEREWOLF_CHAT_DURATION.getKey())* 20L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDayForWereWolf(DayEvent event) {

        if(!isWereWolf()) return;

        if(!getPlayerWW().isState(StatePlayer.ALIVE)){
            return;
        }

        getPlayerWW().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

    }



    @Override
    public void setSolitary(boolean solitary) {
        this.solitary=solitary;
    }

    @Override
    public boolean isSolitary() {
        return solitary;
    }

    @EventHandler
    public void onDetectVictoryNeutral(WinConditionsCheckEvent event) {

        if(!isNeutral()) return;

        if (event.isCancelled()) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (game.getScore().getPlayerSize() != 1) return;

        event.setCancelled(true);

        event.setVictoryTeam(getKey());
    }
    @Override
    public String getDeathRole(){
        return this.deathRole.isEmpty()?getKey():this.deathRole;
    }


    @Override
    public void setDeathRole(String role){
        this.deathRole=role;
    }
}