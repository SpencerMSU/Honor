package msu.msuteam.client;

import msu.msuteam.HonorMod;
import msu.msuteam.honor.HonorSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;

public class HonorHud implements HudRenderCallback {
    @Override
    public void onHudRender(GuiGraphics context, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (client.options.hideGui) return;

        Player player = client.player;
        if (!player.hasAttached(HonorMod.HONOR)) return;

        int honor = HonorSystem.getHonor(player);

        int width = context.guiWidth();
        int height = context.guiHeight();

        int barWidth = 100;
        int barHeight = 5;
        int x = (width - barWidth) / 2;
        int y = height - 40; // Above hotbar

        // Draw Background (Black border)
        context.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);

        // Draw Gradient
        // 0-500: Red (0xFFFF0000) -> White (0xFFFFFFFF)
        // 500-1000: White (0xFFFFFFFF) -> Blue (0xFF0000FF)

        int midX = x + (barWidth / 2);

        // Left half
        context.fillGradient(x, y, midX, y + barHeight, 0xFFFF0000, 0xFFFFFFFF);
        // Right half
        context.fillGradient(midX, y, x + barWidth, y + barHeight, 0xFFFFFFFF, 0xFF0000FF);

        // Draw Markers (200, 400, 600, 800)
        // 200/1000 = 0.2 * barWidth
        int[] markers = {200, 400, 600, 800};
        for (int m : markers) {
            int mx = x + (int)((m / 1000.0) * barWidth);
            context.fill(mx, y, mx + 1, y + barHeight, 0xFF000000);
        }

        // Draw Indicator/Cursor
        int cursorX = x + (int)((honor / 1000.0) * barWidth);
        // Clamp cursorX
        if (cursorX < x) cursorX = x;
        if (cursorX > x + barWidth) cursorX = x + barWidth;

        // Draw a small green cursor
        context.fill(cursorX - 1, y - 2, cursorX + 1, y + barHeight + 2, 0xFF00FF00);
    }
}
