package cardfactory.com.extremeschnapsen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CardImageView extends android.support.v7.widget.AppCompatImageView {

    private long cardId = 0;
    private boolean enable_20_strike = false;

    public CardImageView(Context context) {
        super(context);
    }

    public CardImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public boolean isEnable_20_strike() {
        return enable_20_strike;
    }

    public void setEnable_20_strike(boolean enable_20_strike) {
        this.enable_20_strike = enable_20_strike;
    }
}
