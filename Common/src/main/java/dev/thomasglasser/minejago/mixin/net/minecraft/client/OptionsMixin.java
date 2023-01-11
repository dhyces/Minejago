package dev.thomasglasser.minejago.mixin.net.minecraft.client;

import dev.thomasglasser.minejago.client.MinejagoKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.main.GameConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Options.class)
public class OptionsMixin
{
    @Shadow @Final public KeyMapping[] keyMappings;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void Options(Minecraft minecraft, File file, CallbackInfo ci)
    {
        MinejagoKeyMappings.init();

        ArrayUtils.add(keyMappings, MinejagoKeyMappings.ACTIVATE_SPINJITZU);
    }
}
