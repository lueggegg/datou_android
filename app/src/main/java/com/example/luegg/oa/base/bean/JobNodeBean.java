package com.example.luegg.oa.base.bean;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;

import com.example.luegg.oa.base.CommonUtil;

import java.util.List;

/**
 * Created by luegg on 2017/12/13.
 */
public class JobNodeBean extends BaseBean {
    public int type;
    public String time;
    public String content;
    public String extend;
    public String account;
    public String sender;
    public String dept;
    public int rec_set;
    public boolean has_attachment;
    public List<AttachmentBean> attachment;
    public boolean has_img;
    public List<AttachmentBean> img_attachment;

    private CharSequence parsedContent;
    private CharSequence parsedExtend;
    private CharSequence parsedAttachment;

    public static String parseContent(String content) {
        if (content == null) return  "";

        content = content.substring(1, content.length()-1);
        if (content.equals("")) return  "";

        content = parseHtml(content);
        content = content.replaceAll("\\{\\*(.*?)\\*\\}", "<font color='blue'>$1</font>");
        return content;
    }

    public static String parseHtml(String content) {
        String [][] tags = {
                {"&", "&amp;"},
                {" ", "&nbsp;"},
                {"<", "&lt;"},
                {">", "&gt;"},
                {"\"", "&quot;"},
                {"\n", "<br>"},
                {"\r", "<br>"}
        };
        for (String[] tag : tags) {
            content = content.replace(tag[0], tag[1]);
        }
        return content;
    }

    public CharSequence parseContent() {
        if (parsedContent == null) {
            parsedContent = Html.fromHtml(parseContent(content));
        }
        return  parsedContent;
    }

    public CharSequence parseExtend() {
        if (parsedExtend == null) {
            if (extend.isEmpty() || extend.length() == 2) parsedExtend = "";
            else {
                String str = extend.substring(1, extend.length() - 1);
                parsedExtend = str.replaceAll("\\{\\*(.*?)\\*\\}\n", "");
            }
            if (parsedExtend.charAt(parsedExtend.length() - 1) == '\n') {
                parsedExtend = parsedExtend.subSequence(0, parsedExtend.length() - 1);
            }
        }
        return  parsedExtend;
    }

    public CharSequence parseAttachment() {
        if (parsedAttachment == null) {
            if (attachment != null && attachment.size() > 0) {
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder();
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#098005"));
                for (AttachmentBean bean : attachment) {
                    SpannableString spannableString = new SpannableString(bean.name);
                    spannableString.setSpan(new URLSpan(CommonUtil.getWholeUrl(bean.path)), 0, bean.name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(colorSpan, 0, bean.name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssBuilder.append(spannableString);
                    ssBuilder.append("\n");
                }
                parsedAttachment = ssBuilder;
            } else {
                parsedAttachment = "";
            }
        }
        return parsedAttachment;
    }
}
