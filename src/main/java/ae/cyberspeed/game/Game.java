package ae.cyberspeed.game;

import ae.cyberspeed.reward.Reward;

public interface Game {

    void start();

    void generateResults();

    Reward calculateReward(double bettingAmount);

}
