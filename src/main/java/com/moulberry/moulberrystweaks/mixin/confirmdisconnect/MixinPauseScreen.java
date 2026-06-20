package com.moulberry.moulberrystweaks.mixin.confirmdisconnect;

import com.moulberry.moulberrystweaks.MoulberrysTweaks;
import com.moulberry.moulberrystweaks.Translations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class MixinPauseScreen extends Screen {

    @Shadow
    @Nullable
    private Button disconnectButton;

    @Unique
    private boolean confirmingDisconnect = false;

    protected MixinPauseScreen(Component title) {
        super(title);
    }

    @Redirect(
            method = "lambda$createPauseMenu$7",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;disconnectFromWorld(Lnet/minecraft/network/chat/Component;)V"
            )
    )
    public void onDisconnect(Minecraft instance, Component message) {
        if (MoulberrysTweaks.config.gameplay.confirmDisconnect && this.disconnectButton != null && !this.confirmingDisconnect && !this.minecraft.isLocalServer()) {
            this.disconnectButton.active = true;
            this.disconnectButton.setMessage(Translations.CONFIRM_DISCONNECT);
            this.confirmingDisconnect = true;
        } else {
            instance.disconnectFromWorld(message);
        }
    }

}
