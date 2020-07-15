package uk.co.harieo.quackbedwars.teams;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uk.co.harieo.minigames.teams.Team;
import uk.co.harieo.quackbedwars.egg.EggData;
import uk.co.harieo.quackbedwars.teams.upgrades.currency.BaseCurrencyUpgrade;
import uk.co.harieo.quackbedwars.teams.upgrades.currency.CurrencyUpgrade;
import uk.co.harieo.quackbedwars.teams.upgrades.effects.PurchasablePotionEffect;
import uk.co.harieo.quackbedwars.teams.upgrades.traps.PurchasableTraps;

public class TeamGameData {

	private static final Map<Team, TeamGameData> data = new HashMap<>();

	private final Set<PurchasableTraps> purchasedTraps = new HashSet<>();
	private final Set<PurchasablePotionEffect> purchasedPotionEffects = new HashSet<>();
	private CurrencyUpgrade currencyUpgrade = BaseCurrencyUpgrade.INSTANCE;
	private EggData eggData;
	private int playersAlive = 0;

	/**
	 * A class which stores basic game data for a {@link BedWarsTeamData}
	 *
	 * @param team which this data represents
	 */
	public TeamGameData(Team team) {
		data.put(team, this);
	}

	/**
	 * @return whether this team's egg is intact
	 */
	public boolean isEggIntact() {
		return eggData != null && eggData.isIntact();
	}

	/**
	 * @return the data for this team's egg
	 */
	public EggData getEggData() {
		return eggData;
	}

	/**
	 * Sets the data for this team's egg
	 *
	 * @param eggData to be set
	 */
	public void setEggData(EggData eggData) {
		this.eggData = eggData;
	}

	/**
	 * @return all traps which this team currently owns in an immutable set
	 */
	public Set<PurchasableTraps> getPurchasedTraps() {
		return ImmutableSet.copyOf(purchasedTraps);
	}

	/**
	 * Adds a trap to the set of this team's purchased traps
	 *
	 * @param trapUpgrade to add
	 */
	public void addPurchasedTrap(PurchasableTraps trapUpgrade) {
		purchasedTraps.add(trapUpgrade);
	}

	/**
	 * Removes a trap from this team's set of purchased traps
	 *
	 * @param trapUpgrade to be removed
	 */
	public void removePurchasedTrap(PurchasableTraps trapUpgrade) {
		purchasedTraps.remove(trapUpgrade);
	}

	/**
	 * @return a set of all owned potion effects
	 */
	public Set<PurchasablePotionEffect> getPurchasedPotionEffects() {
		return purchasedPotionEffects;
	}

	/**
	 * Add a new potion effect to the set of purchased effects
	 *
	 * @param potionEffect to be added
	 */
	public void addPurchasedPotionEffect(PurchasablePotionEffect potionEffect) {
		purchasedPotionEffects.add(potionEffect);
	}

	/**
	 * Remove a potion effect from the set of purchased effects
	 *
	 * @param potionEffect to be removed
	 */
	public void removePurchasedPotionEffect(PurchasablePotionEffect potionEffect) {
		purchasedPotionEffects.remove(potionEffect);
	}

	/**
	 * @return the current {@link CurrencyUpgrade} which this team is on
	 */
	public CurrencyUpgrade getCurrencyUpgrade() {
		return currencyUpgrade;
	}

	/**
	 * Sets the current currency upgrade this team has purchased
	 *
	 * @param currencyUpgrade which the team owns
	 */
	public void setCurrencyUpgrade(CurrencyUpgrade currencyUpgrade) {
		this.currencyUpgrade = currencyUpgrade;
	}

	/**
	 * @return the amount of players alive on this team
	 */
	public int getPlayersAlive() {
		return playersAlive;
	}

	/**
	 * Adds 1 to the amount of players alive on this team
	 */
	public void incrementPlayersAlive() {
		setPlayersAlive(getPlayersAlive() + 1);
	}

	/**
	 * Subtracts 1 from the amount of players alie on this team
	 */
	public void decrementPlayersAlive() {
		setPlayersAlive(getPlayersAlive() - 1);
	}

	/**
	 * Sets the amount of players alive on this team
	 *
	 * @param playersAlive amount to set to
	 */
	public void setPlayersAlive(int playersAlive) {
		this.playersAlive = playersAlive;
	}

	/**
	 * Retrieves the single game data for the specified team. No team will have more than 1 instance of this class at
	 * any point.
	 *
	 * @param team to get the game data for
	 * @return the game data for the team, either from the cache or new if this is the first call
	 */
	public static TeamGameData getGameData(Team team) {
		if (data.containsKey(team)) {
			return data.get(team);
		} else {
			return new TeamGameData(team);
		}
	}

}
