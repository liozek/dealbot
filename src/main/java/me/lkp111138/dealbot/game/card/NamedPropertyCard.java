package me.lkp111138.dealbot.game.card;

import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.CardArgumentRequest;
import me.lkp111138.dealbot.game.Player;
import me.lkp111138.dealbot.game.card.state.CardState;
import me.lkp111138.dealbot.game.card.state.CardStateInMainDeck;
import me.lkp111138.dealbot.game.card.state.CardStateInPlayerProperty;

public class NamedPropertyCard implements PropertyCard {
    private int id;
    private int colour;
    private int value;
    private CardState state = new CardStateInMainDeck();

    public NamedPropertyCard(int id, int colour, int value) {
        this.id = id;
        this.colour = colour;
        this.value = value;
    }

    @Override
    public String getNameKey() {
        return "card.name.property." + id;
    }

    @Override
    public String getDescriptionKey() {
        return "card.desc.property";
    }

    @Override
    public CardState getState() {
        return state;
    }

    @Override
    public int getCurrencyValue() {
        return value;
    }

    @Override
    public CardArgumentRequest execute(DealBot bot, Player player, String[] arg) {
        setState(new CardStateInPlayerProperty(player, colour));
        return null;
    }

    public void setState(CardState state) {
        this.state = state;
    }
}