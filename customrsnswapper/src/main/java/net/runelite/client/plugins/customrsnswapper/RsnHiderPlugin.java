package net.runelite.client.plugins.customrsnswapper;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.customrsnswapper.RsnHiderConfig;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
   name = "RSN Hider",
   description = "Hides your rsn for streamers.",
   tags = {"twitch"},
   enabledByDefault = false
)
public class RsnHiderPlugin extends Plugin {
   @Inject
   private Client client;
   @Inject
   private ClientThread clientThread;
   @Inject
   private RsnHiderConfig config;
   private static String fakeRsn;
   private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

   @Provides
   private RsnHiderConfig getConfig(ConfigManager configManager) {
      return (RsnHiderConfig)configManager.getConfig(RsnHiderConfig.class);
   }

   public void startUp() {
      this.setFakeRsn();
   }

   public void shutDown() {
      this.clientThread.invokeLater(() -> {
         this.client.runScript(new Object[]{223});
      });
   }

   @Subscribe
   public void onConfigChanged(ConfigChanged event) {
      if (event.getGroup().equals("rsnhider")) {
         this.setFakeRsn();
      }
   }

   @Subscribe
   private void onBeforeRender(BeforeRender event) {
      if (this.client.getGameState() == GameState.LOGGED_IN) {
         if (this.config.hideWidgets()) {
            Widget[] var2 = this.client.getWidgetRoots();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Widget widgetRoot = var2[var4];
               this.processWidget(widgetRoot);
            }
         } else {
            this.updateChatbox();
         }

      }
   }

   private void setFakeRsn() {
      fakeRsn = this.config.customRsn().equals("") ? randomAlphaNumeric(12) : this.config.customRsn();
   }

   private void processWidget(Widget widget) {
      if (widget != null) {
         if (widget.getText() != null) {
            widget.setText(this.replaceRsn(widget.getText()));
         }

         Widget[] var2 = widget.getStaticChildren();
         int var3 = var2.length;

         int var4;
         Widget nestedChild;
         for(var4 = 0; var4 < var3; ++var4) {
            nestedChild = var2[var4];
            this.processWidget(nestedChild);
         }

         var2 = widget.getDynamicChildren();
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            nestedChild = var2[var4];
            this.processWidget(nestedChild);
         }

         var2 = widget.getNestedChildren();
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            nestedChild = var2[var4];
            this.processWidget(nestedChild);
         }

      }
   }

   private void updateChatbox() {
      Widget chatboxTypedText = this.client.getWidget(WidgetInfo.CHATBOX_INPUT);
      if (chatboxTypedText != null && !chatboxTypedText.isHidden()) {
         String[] chatbox = chatboxTypedText.getText().split(":", 2);
         String playerRsn = Text.toJagexName(this.client.getLocalPlayer().getName());
         if (Text.standardize(chatbox[0]).contains(Text.standardize(playerRsn))) {
            chatbox[0] = fakeRsn;
         }

         chatboxTypedText.setText(chatbox[0] + ":" + chatbox[1]);
      }
   }

   @Subscribe
   private void onChatMessage(ChatMessage event) {
      if (this.client.getLocalPlayer().getName() != null) {
         String replaced = this.replaceRsn(event.getMessage());
         event.setMessage(replaced);
         event.getMessageNode().setValue(replaced);
         if (event.getName() != null) {
            boolean isLocalPlayer = Text.standardize(event.getName()).equalsIgnoreCase(Text.standardize(this.client.getLocalPlayer().getName()));
            if (isLocalPlayer) {
               event.setName(fakeRsn);
               event.getMessageNode().setName(fakeRsn);
            }

         }
      }
   }

   @Subscribe
   private void onOverheadTextChanged(OverheadTextChanged event) {
      event.getActor().setOverheadText(this.replaceRsn(event.getOverheadText()));
   }

   private String replaceRsn(String textIn) {
      String playerRsn = Text.toJagexName(this.client.getLocalPlayer().getName());

      String partOne;
      String partTwo;
      for(String standardized = Text.standardize(playerRsn); Text.standardize(textIn).contains(standardized); textIn = partOne + fakeRsn + partTwo) {
         int idx = textIn.replace(" ", " ").toLowerCase().indexOf(playerRsn.toLowerCase());
         int length = playerRsn.length();
         partOne = textIn.substring(0, idx);
         partTwo = textIn.substring(idx + length);
      }

      return textIn;
   }

   private static String randomAlphaNumeric(int count) {
      StringBuilder builder = new StringBuilder();
      int var2 = count;

      while(var2-- != 0) {
         int character = (int)(Math.random() * (double)"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
         builder.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(character));
      }

      return builder.toString();
   }
}
