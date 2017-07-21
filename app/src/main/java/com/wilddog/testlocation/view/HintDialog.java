package com.wilddog.testlocation.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.TextView;

import com.wilddog.testlocation.R;


/**
 * Created by he on 2017/7/7.
 */

public class HintDialog extends Dialog {

    private TextView title;
    private TextView content;
    private Builder builder;
    private TextView btnOk;

    public HintDialog(@NonNull Context context) {
        super(context);
    }

    public HintDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected HintDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private HintDialog(Context context, Builder builder) {
        super(context, R.style.MyDialog);
        this.builder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.dialog_hint);
        title = (TextView) findViewById(R.id.hint_title);
        content = (TextView) findViewById(R.id.content);
        btnOk = (TextView) findViewById(R.id.dg_confirm);

        title.setText(builder.title);
        content.setText(builder.contents);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public static class Builder{

        private Context context;
        private String title;
        private String contents;

        public Builder(Context context){
            this.context = context;
        }
        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder setContents(String contents){
            this.contents = contents;
            return this;
        }

        public void builder(){
            new HintDialog(context,this).show();
        }
    }
}
