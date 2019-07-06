package nju.androidchat.client.component;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.util.UUID;

import lombok.Setter;
import nju.androidchat.client.R;

public class ItemTextSend extends LinearLayout implements View.OnLongClickListener {
    @StyleableRes
    int index0 = 0;

    private TextView textView;
    private Context context;
    private UUID messageId;
    @Setter
    private OnRecallMessageRequested onRecallMessageRequested;

    private final static String img_pattern = "!\\[.+\\]\\(.+\\)";

    public ItemTextSend(Context context, String text, UUID messageId, OnRecallMessageRequested onRecallMessageRequested) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_send, this);
        this.textView = findViewById(R.id.chat_item_content_text);
        this.messageId = messageId;
        this.onRecallMessageRequested = onRecallMessageRequested;

        this.setOnLongClickListener(this);
        setText(text);
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

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定要撤回这条消息吗？")
                .setPositiveButton("是", (dialog, which) -> {
                    if (onRecallMessageRequested != null) {
                        onRecallMessageRequested.onRecallMessageRequested(this.messageId);
                    }
                })
                .setNegativeButton("否", ((dialog, which) -> {
                }))
                .create()
                .show();

        return true;


    }

}
