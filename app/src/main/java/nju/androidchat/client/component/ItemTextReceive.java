package nju.androidchat.client.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.util.UUID;

import nju.androidchat.client.R;

public class ItemTextReceive extends LinearLayout {


    @StyleableRes
    int index0 = 0;

    private TextView textView;
    private Context context;
    private UUID messageId;
    private OnRecallMessageRequested onRecallMessageRequested;

    private final static String img_pattern = "!\\[.+\\]\\(.+\\)";

    public ItemTextReceive(Context context, String text, UUID messageId) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_receive, this);
        this.textView = findViewById(R.id.chat_item_content_text);
        this.messageId = messageId;
        setText(text);
    }

    public void init(Context context) {

    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setText(String text) {
        if (text.matches(img_pattern)) {
            Runnable runnable = () -> {
                SpannableString spannableString = new SpannableString("   ");
                String url = text.substring(text.indexOf("(") + 1, text.length() - 1);
                System.out.println(url);
                try {
                    Drawable drawable = Drawable.createFromStream(new java.net.URL(url).openStream(), null);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 5, drawable.getIntrinsicHeight() * 5);
                    spannableString.setSpan(new ImageSpan(drawable), 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.obj = spannableString;
                handler.sendMessage(msg);
            };
            String str = text.substring(text.indexOf("[") + 1, text.indexOf("]"));
            textView.setText(str);
            new Thread(runnable).start();
        } else {
            textView.setText(text);
        }

    }

    private Handler handler = new Handler(msg -> {
        SpannableString spannableString = (SpannableString) msg.obj;
        textView.setText(spannableString);
        return true;
    });
}
