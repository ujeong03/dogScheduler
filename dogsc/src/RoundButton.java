import javax.swing.*;
import java.awt.*;

/**
 * 둥근 모양의 버튼으로 바꾸기 위한 클래스
 * https://leirbag.tistory.com/15 에서 참조한 코드입니다.
 */
public class RoundButton extends JButton {
    public RoundButton(String text) { super(text); decorate(); }

    protected void decorate() { setBorderPainted(false); setOpaque(false); }
    @Override
    protected void paintComponent(Graphics g) {
        Color c=new Color(215, 214, 254); //배경색 결정
        Color o=new Color(3, 3, 3); //글자색 결정
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getModel().isArmed()) { graphics.setColor(c.darker()); }
        else if (getModel().isRollover()) { graphics.setColor(c.brighter()); }
        else { graphics.setColor(c); }
        graphics.fillRoundRect(0, 0, width, height, 10, 10);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();
        int textX = (width - stringBounds.width) / 2;
        int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
        graphics.setColor(o);
        graphics.setFont(getFont());
        graphics.drawString(getText(), textX, textY);
        graphics.dispose();
        super.paintComponent(g);
    }
}
