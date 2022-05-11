package fr.ph1lou.werewolfapi.game;

/**
 * @author Ph1Lou
 */

@SuppressWarnings("unused")

public interface IConfiguration {

    int getDiamondLimit();

    void setDiamondLimit(int diamond_limit);

    int getStrengthRate();

    void setStrengthRate(int strengthRate);

    int getPlayerRequiredVoteEnd();

    void setPlayerRequiredVoteEnd(int player_required_before_voting_ends);

    int getPearlRate() ;

    void setPearlRate(int pearlRate);

    int getFlintRate() ;

    void setFlintRate(int flintRate) ;

    int getAppleRate() ;

    void setAppleRate(int appleRate);

    int getXpBoost();

    void setXpBoost(int xpBoost);

    int getLimitProtectionIron() ;

    void setLimitProtectionIron(int limitProtectionIron);

    int getLimitProtectionDiamond() ;

    void setLimitProtectionDiamond(int limitProtectionDiamond);

    int getLimitSharpnessDiamond();

    void setLimitSharpnessDiamond(int limitSharpnessDiamond) ;

    int getLimitSharpnessIron();

    void setLimitSharpnessIron(int limitSharpnessIron);

    int getLimitPowerBow() ;

    void setLimitPowerBow(int limitPowerBow);

    int getLimitKnockBack() ;

    void setLimitKnockBack(int limitKnockBack);

    int getLimitPunch() ;

    void setLimitPunch(int limitPunch);

    int getGoldenAppleParticles() ;

    void setGoldenAppleParticles(int goldenAppleParticles);

    int getResistanceRate() ;

    void setResistanceRate(int resistanceRate);

    boolean isTrollSV();

    void setTrollSV(boolean trollSV);

    int getBorderMax();

    void setBorderMax(int borderMax);

    int getBorderMin();

    void setBorderMin(int borderMin);

    int getTimerValue(String key);

    boolean isConfigActive(String key);

    int getRoleCount(String key);

    int getLoverCount(String key);

    void setLoverCount(String key,int value);

    void addOneLover(String key);

    void removeOneLover(String key);

    boolean isScenarioActive(String key);

    double getBorderSpeed();

    void setBorderSpeed(double seed);

    void switchConfigValue(String key);

    void switchScenarioValue(String key);

    void removeOneRole(String key);

    void addOneRole(String key);

    void setRole(String key, int value);

    void moveTimer(String key, int value);

    void setConfig(String key,boolean enable);

    void setScenario(String key,boolean enable);

    int getLimitDepthStrider();

    void setTimerValue(String key, int value);

    void setLimitDepthStrider(int limitDepthRider);

    /**
     * return true if the whitelist is on
     * @return boolean
     */

    boolean isWhiteList();

    /**
     * change the status of the whitelist
     * @param whiteList the desired status
     */

    void setWhiteList(boolean whiteList);

    /**
     * return the maximum of player accepted in the game
     * @return int
     */

    int getPlayerMax();

    /**
     * Set the maximum number of players
     * @param playerMax maximum number of players

     */

    void setPlayerMax(int playerMax);

    /**
     * return the spectator Mode of the game (0 : disable, 1 : for death players, 2: for all players)
     * @return int
     */

    int getSpectatorMode();

    /**
     * change the status of the Spectator Mode
     * @param spectatorMode the desired status (0 : disable, 1 : for death players, 2: for all players)
     */

    void setSpectatorMode(int spectatorMode);

    String getTrollKey();

    void setTrollKey(String trollKey);

    int getKnockBackMode();

    /**
     * Get Random Event Probability
     * @param key of the random event
     * @return the probability
     */
    int getProbability(String key);

    /**
     * Set Probability of the random Event
     * @param key of the random event
     * @param probability the probability
     */
    void setProbability(String key,int probability);

    void setKnockBackMode(int knockBackMode);

    int getWereWolfChatMaxMessage();

    void setWereWolfChatMaxMessage(int nbMessage);

    boolean isTrollLover();

    void setTrollLover(boolean troll);

    int getValue(String baseKey, String key);

    void setValue(String baseKey, String key, int value);

    boolean isMeetUp();

    void setMeetUp(boolean meetUp);
}