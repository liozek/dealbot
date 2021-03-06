package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.game.cards.Card;
import me.lkp111138.dealbot.game.cards.PropertyCard;
import me.lkp111138.dealbot.translation.Translation;

import java.util.ArrayList;
import java.util.List;

/**
 * The 'Deal Breaker' card. Takes one complete set of property from an opponent.
 */
public class DealBreakerActionCard extends ActionCard {
    public DealBreakerActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public int currencyValue() {
        return 5;
    }

    @Override
    public String getDescription() {
        return translation.DEAL_BREAKER_DESC();
    }

    @Override
    public String getCardFunctionalTitle() {
        return translation.DEAL_BREAKER();
    }

    @Override
    public void use(GamePlayer player, String[] args) {
        int nonce = player.getGame().nextNonce();
        if (args.length == 0) {
            // choose player
            SlyDealActionCard.playerChooser(player, nonce, translation.CANCEL(), translation.WHOSE_DEAL_TO_BREAK());
        }
        if (args.length == 1) {
            // player chosen, choose property
            int index = Integer.parseInt(args[0]);
            GamePlayer victim = player.getGame().getGamePlayers().get(index);
            List<InlineKeyboardButton[]> buttons = new ArrayList<>();
            for (Integer group : victim.getPropertyDecks().keySet()) {
                if (PropertyCard.realCount(victim.getPropertyDecks().get(group)) >= PropertyCard.propertySetCounts[group]) {
                    // is a full set
                    buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(translation.PROPERTY_GROUP(group))
                            .callbackData(nonce + ":card_arg:" + index + ":" + group)});
                }
            }
            buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(translation.CANCEL()).callbackData(nonce + ":use_cancel")});
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.WHOSE_DEAL_TO_BREAK());
            edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
            player.getGame().execute(edit);
        }
        if (args.length == 2) {
            // ask if they were to say no about us breaking the deal
            int index = Integer.parseInt(args[0]);
            int group = Integer.parseInt(args[1]);
            GamePlayer victim = player.getGame().getGamePlayers().get(index);
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.YOU_HAVE_USED_AGAINST(getCardFunctionalTitle(), victim.getName()));
            player.getGame().execute(edit);
            SendMessage send = new SendMessage(player.getGame().getGid(), translation.SOMEONE_HAVE_USED_AGAINST(player.getName(), getCardFunctionalTitle(), victim.getName()));
            player.getGame().execute(send);
            victim.promptSayNo(0, translation.DEAL_BREAKER_SAY_NO_PROMPT(player.getName(), group), () -> {
                DealBot.triggerAchievement(victim.getTgid(), DealBot.Achievement.YOUR_PROPERTY_ISNT_YOURS);
                List<Card> cards = victim.getPropertyDecks().get(group);
                List<Card> props = player.getPropertyDecks().getOrDefault(group, new ArrayList<>());
                props.addAll(cards);
                player.getPropertyDecks().put(group, props);
                victim.getPropertyDecks().remove(group);
                player.getGame().resumeTurn();
            }, player.getGame()::resumeTurn, player);
        }
    }
}
