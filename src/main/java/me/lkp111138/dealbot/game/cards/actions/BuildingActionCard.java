package me.lkp111138.dealbot.game.cards.actions;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import me.lkp111138.dealbot.DealBot;
import me.lkp111138.dealbot.game.GamePlayer;
import me.lkp111138.dealbot.game.cards.ActionCard;
import me.lkp111138.dealbot.game.cards.PropertyCard;
import me.lkp111138.dealbot.translation.Translation;

import java.util.ArrayList;
import java.util.List;

public abstract class BuildingActionCard extends ActionCard {
    BuildingActionCard(Translation translation) {
        super(translation);
    }

    @Override
    public final void use(GamePlayer player, String[] args) {
        if (this instanceof HouseActionCard) {
            DealBot.triggerAchievement(player.getTgid(), DealBot.Achievement.MANSION);
        }
        if (this instanceof HotelActionCard) {
            DealBot.triggerAchievement(player.getTgid(), DealBot.Achievement.HOTEL_MANAGER);
        }
        if (args.length == 0) {
            // choose group
            List<InlineKeyboardButton[]> buttons = new ArrayList<>();
            int nonce = player.getGame().nextNonce();
            for (Integer group : player.getPropertyDecks().keySet()) {
                if (PropertyCard.realCount(player.getPropertyDecks().get(group)) >= PropertyCard.propertySetCounts[group] && group < 8) {
                    buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(translation.PROPERTY_GROUP(group))
                            .callbackData(nonce + ":card_arg:" + group)});
                }
            }
            buttons.add(new InlineKeyboardButton[]{new InlineKeyboardButton(translation.CANCEL())
                    .callbackData(nonce + ":use_cancel")});
            EditMessageText edit = new EditMessageText(player.getTgid(), player.getMessageId(), translation.BUILD_THIS_ON(getCardFunctionalTitle()));
            edit.replyMarkup(new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0][0])));
            player.getGame().execute(edit);
        }
        if (args.length == 1) {
            // group chosen, add to list
            int group = Integer.parseInt(args[0]);
            player.addBuilding(this, group);
            player.promptForCard();
        }
    }
}
