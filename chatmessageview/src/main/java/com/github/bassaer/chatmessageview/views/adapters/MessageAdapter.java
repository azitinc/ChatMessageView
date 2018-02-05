package com.github.bassaer.chatmessageview.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.bassaer.chatmessageview.R;
import com.github.bassaer.chatmessageview.model.IChatUser;
import com.github.bassaer.chatmessageview.models.Attribute;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.views.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Custom list adapter for the chat timeline
 * Created by nakayama on 2016/08/08.
 */
public class MessageAdapter extends ArrayAdapter<Object> {

    private LayoutInflater mLayoutInflater;
    private List<Object> mObjects;
    private List<Object> mViewTypes = new ArrayList<>();

    private Message.OnIconClickListener mOnIconClickListener;
    private Message.OnBubbleClickListener mOnBubbleClickListener;
    private Message.OnIconLongClickListener mOnIconLongClickListener;
    private Message.OnBubbleLongClickListener mOnBubbleLongClickListener;

    private int mUsernameTextColor = ContextCompat.getColor(getContext(), R.color.blueGray500);
    private int mSendTimeTextColor = ContextCompat.getColor(getContext(), R.color.blueGray500);
    private int mDateSeparatorColor = ContextCompat.getColor(getContext(), R.color.blueGray500);
    private int mRightMessageTextColor = Color.WHITE;
    private int mLeftMessageTextColor = Color.BLACK;
    private int mLeftBubbleColor;
    private int mRightBubbleColor;
    private int mStatusColor = ContextCompat.getColor(getContext(), R.color.blueGray500);

    private int mLeftPictureWidth = getContext().getResources().getDimensionPixelSize(R.dimen.width_normal);
    private int mLeftPictureHeight = getContext().getResources().getDimensionPixelSize(R.dimen.width_normal);
    private int mRightPictureWidth = getContext().getResources().getDimensionPixelSize(R.dimen.width_normal);
    private int mRightPictureHeight = getContext().getResources().getDimensionPixelSize(R.dimen.width_normal);

    /**
     * Default message item margin top
     */
    private int mMessageTopMargin = 5;
    /**
     * Default message item margin bottom
     */
    private int mMessageBottomMargin = 5;

    private Attribute mAttribute;

    public MessageAdapter(Context context, int resource, List<Object> objects, Attribute attribute) {
        super(context, resource, objects);
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObjects = objects;
        mViewTypes.add(String.class);
        mViewTypes.add(Message.class);
        mLeftBubbleColor = ContextCompat.getColor(context, R.color.default_left_bubble_color);
        mRightBubbleColor = ContextCompat.getColor(context, R.color.default_right_bubble_color);
        mAttribute = attribute;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mObjects.get(position);
        return mViewTypes.indexOf(item);
    }

    @Override
    public int getViewTypeCount() {
        return mViewTypes.size();
    }

    @NonNull
    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Object item = getItem(position);

        if (item instanceof String) {
            // item is Date label
            DateViewHolder dateViewHolder;
            String dateText = (String) item;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.date_cell, null);
                dateViewHolder = new DateViewHolder();
                dateViewHolder.dateSeparatorText = convertView.findViewById(R.id.date_separate_text);
                convertView.setTag(dateViewHolder);
            } else {
                dateViewHolder = (DateViewHolder) convertView.getTag();
            }
            dateViewHolder.dateSeparatorText.setText(dateText);
            dateViewHolder.dateSeparatorText.setTextColor(mDateSeparatorColor);
            dateViewHolder.dateSeparatorText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttribute.getDateSeparatorFontSize());
        } else {
            //Item is a message
            MessageViewHolder holder;
            final Message message = (Message) item;
            if (position > 0) {
                Object prevItem = getItem(position - 1);
                if (prevItem instanceof Message) {
                    final Message prevMessage = (Message) prevItem;
                    if (prevMessage.getUser().getId().equals(message.getUser().getId())) {
                        //If send same person, hide username and icon.
                        message.setIconVisibility(false);
                        message.setUsernameVisibility(false);
                    }
                }
            }

            IChatUser user = message.getUser();

            if (message.isRightMessage()) {
                //Right message
                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.message_view_right, null);
                    holder = new MessageViewHolder();
                    holder.iconContainer = convertView.findViewById(R.id.user_icon_container);
                    holder.mainMessageContainer = convertView.findViewById(R.id.main_message_container);
                    holder.timeText = convertView.findViewById(R.id.time_label_text);
                    holder.usernameContainer = convertView.findViewById(R.id.message_user_name_container);
                    holder.statusContainer = convertView.findViewById(R.id.message_status_container);
                    convertView.setTag(holder);
                } else {
                    holder = (MessageViewHolder) convertView.getTag();
                }

                //Remove view in each container
                holder.iconContainer.removeAllViews();
                holder.usernameContainer.removeAllViews();
                holder.statusContainer.removeAllViews();
                holder.mainMessageContainer.removeAllViews();

                if (user.getName() != null && message.getUsernameVisibility()) {
                    View usernameView = mLayoutInflater.inflate(R.layout.user_name_right, holder.usernameContainer);
                    holder.username = usernameView.findViewById(R.id.message_user_name);
                    holder.username.setText(user.getName());
                    holder.username.setTextColor(mUsernameTextColor);
                    holder.username.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttribute.getUsernameFontSize());
                }

                // if false, icon is not shown.
                if (!message.isIconHided()) {
                    View iconView = mLayoutInflater.inflate(R.layout.user_icon_right, holder.iconContainer);
                    holder.icon = iconView.findViewById(R.id.user_icon);
                    if (message.getIconVisibility()) {
                        //if false, set default icon.
                        if (user.getIcon() != null) {
                            holder.icon.setImageBitmap(user.getIcon());
                        }

                    } else {
                        //Show nothing
                        holder.icon.setVisibility(View.INVISIBLE);
                    }
                }


                //Show message status
                if (message.getMessageStatusType() == Message.MESSAGE_STATUS_ICON || message.getMessageStatusType() == Message.MESSAGE_STATUS_ICON_RIGHT_ONLY) {
                    //Show message status icon
                    View statusIcon = mLayoutInflater.inflate(R.layout.message_status_icon, holder.statusContainer);
                    holder.statusIcon = statusIcon.findViewById(R.id.status_icon_image_view);
                    holder.statusIcon.setImageDrawable(message.getStatusIcon());
                    setColorDrawable(mStatusColor, holder.statusIcon.getDrawable());
                } else if (message.getMessageStatusType() == Message.MESSAGE_STATUS_TEXT || message.getMessageStatusType() == Message.MESSAGE_STATUS_TEXT_RIGHT_ONLY) {
                    //Show message status text
                    View statusText = mLayoutInflater.inflate(R.layout.message_status_text, holder.statusContainer);
                    holder.statusText = statusText.findViewById(R.id.status_text_view);
                    holder.statusText.setText(message.getStatusText());
                    holder.statusText.setTextColor(mStatusColor);
                }

                //Set text or picture on message bubble
                switch (message.getType()) {
                    case PICTURE:
                        //Set picture
                        if (message.isRightMessage()) {
                            View pictureBubble = mLayoutInflater.inflate(R.layout.message_picture_right, holder.mainMessageContainer);
                            holder.messagePicture = pictureBubble.findViewById(R.id.message_picture);
                            holder.messagePicture.setImageBitmap(message.getPicture());
                            ViewGroup.LayoutParams params = holder.mainMessageContainer.findViewById(R.id.right_message_picture_wrapper).getLayoutParams();
                            params.width = mRightPictureWidth;
                            params.height = mRightPictureHeight;
                        } else {
                            View pictureBubble = mLayoutInflater.inflate(R.layout.message_picture_left, holder.mainMessageContainer);
                            holder.messagePicture = pictureBubble.findViewById(R.id.message_picture);
                            holder.messagePicture.setImageBitmap(message.getPicture());
                            ViewGroup.LayoutParams params = holder.mainMessageContainer.findViewById(R.id.left_message_picture_wrapper).getLayoutParams();
                            params.width = mLeftPictureWidth;
                            params.height = mLeftPictureHeight;
                        }
                        break;
                    case LINK:
                        //Set text
                        View linkBubble = mLayoutInflater.inflate(R.layout.message_link_right, holder.mainMessageContainer);
                        holder.messageLink = linkBubble.findViewById(R.id.message_link);
                        holder.messageLink.setText(message.getMessageText());
                        //Set bubble color
                        setColorDrawable(mRightBubbleColor, holder.messageLink.getBackground());
                        //Set message text color
                        holder.messageLink.setTextColor(mRightMessageTextColor);
                        break;
                    case TEXT:
                    default:
                        //Set text
                        View textBubble = mLayoutInflater.inflate(R.layout.message_text_right, holder.mainMessageContainer);
                        holder.messageText = textBubble.findViewById(R.id.message_text);
                        holder.messageText.setText(message.getMessageText());
                        //Set bubble color
                        setColorDrawable(mRightBubbleColor, holder.messageText.getBackground());
                        //Set message text color
                        holder.messageText.setTextColor(mRightMessageTextColor);
                        break;

                }

                holder.timeText.setText(message.getTimeText());

                holder.timeText.setTextColor(mSendTimeTextColor);

                //Set Padding
                convertView.setPadding(0, mMessageTopMargin, 0, mMessageBottomMargin);

            } else {
                //Left message
                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.message_view_left, null);
                    holder = new MessageViewHolder();
                    holder.iconContainer = convertView.findViewById(R.id.user_icon_container);
                    holder.mainMessageContainer = convertView.findViewById(R.id.main_message_container);
                    holder.timeText = convertView.findViewById(R.id.time_label_text);
                    holder.usernameContainer = convertView.findViewById(R.id.message_user_name_container);
                    holder.statusContainer = convertView.findViewById(R.id.message_status_container);
                    convertView.setTag(holder);
                } else {
                    holder = (MessageViewHolder) convertView.getTag();
                }


                //Remove view in each container
                holder.iconContainer.removeAllViews();
                holder.usernameContainer.removeAllViews();
                holder.statusContainer.removeAllViews();
                holder.mainMessageContainer.removeAllViews();


                if (user.getName() != null && message.getUsernameVisibility()) {
                    View usernameView = mLayoutInflater.inflate(R.layout.user_name_left, holder.usernameContainer);
                    holder.username = usernameView.findViewById(R.id.message_user_name);
                    holder.username.setText(user.getName());
                    holder.username.setTextColor(mUsernameTextColor);
                    holder.username.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttribute.getUsernameFontSize());
                }

                // if false, icon is not shown.
                if (!message.isIconHided()) {
                    View iconView = mLayoutInflater.inflate(R.layout.user_icon_left, holder.iconContainer);
                    holder.icon = iconView.findViewById(R.id.user_icon);
                    if (message.getIconVisibility()) {
                        //if false, set default icon.
                        if (user.getIcon() != null) {
                            holder.icon.setImageBitmap(user.getIcon());
                        }
                    } else {
                        //Show nothing
                        holder.icon.setImageBitmap(null);
                    }

                }

                //Show message status
                if (message.getMessageStatusType() == Message.MESSAGE_STATUS_ICON || message.getMessageStatusType() == Message.MESSAGE_STATUS_ICON_LEFT_ONLY) {
                    //Show message status icon
                    View statusIcon = mLayoutInflater.inflate(R.layout.message_status_icon, holder.statusContainer);
                    holder.statusIcon = statusIcon.findViewById(R.id.status_icon_image_view);
                    holder.statusIcon.setImageDrawable(message.getStatusIcon());
                    setColorDrawable(mStatusColor, holder.statusIcon.getDrawable());
                } else if (message.getMessageStatusType() == Message.MESSAGE_STATUS_TEXT || message.getMessageStatusType() == Message.MESSAGE_STATUS_TEXT_LEFT_ONLY) {
                    //Show message status text
                    View statusText = mLayoutInflater.inflate(R.layout.message_status_text, holder.statusContainer);
                    holder.statusText = statusText.findViewById(R.id.status_text_view);
                    holder.statusText.setText(message.getStatusText());
                    holder.statusText.setTextColor(mStatusColor);
                }

                //Set text or picture on message bubble
                switch (message.getType()) {
                    case PICTURE:
                        //Set picture
                        View pictureBubble = mLayoutInflater.inflate(R.layout.message_picture_left, holder.mainMessageContainer);
                        holder.messagePicture = pictureBubble.findViewById(R.id.message_picture);
                        holder.messagePicture.setImageBitmap(message.getPicture());
                        break;
                    case LINK:
                        //Set link
                        View linkBubble = mLayoutInflater.inflate(R.layout.message_link_left, holder.mainMessageContainer);
                        holder.messageLink = linkBubble.findViewById(R.id.message_link);
                        holder.messageLink.setText(message.getMessageText());
                        //Set bubble color
                        setColorDrawable(mLeftBubbleColor, holder.messageLink.getBackground());
                        //Set message text color
                        holder.messageLink.setTextColor(mLeftMessageTextColor);
                        break;
                    case TEXT:
                    default:
                        //Set text
                        View textBubble = mLayoutInflater.inflate(R.layout.message_text_left, holder.mainMessageContainer);
                        holder.messageText = textBubble.findViewById(R.id.message_text);
                        holder.messageText.setText(message.getMessageText());
                        //Set bubble color
                        setColorDrawable(mLeftBubbleColor, holder.messageText.getBackground());
                        //Set message text color
                        holder.messageText.setTextColor(mLeftMessageTextColor);
                        break;

                }

                holder.timeText.setText(message.getTimeText());
                holder.timeText.setTextColor(mSendTimeTextColor);

                //Set Padding
                convertView.setPadding(0, mMessageTopMargin, 0, mMessageBottomMargin);

            }

            if (holder.mainMessageContainer != null) {
                //Set bubble click listener
                if (mOnBubbleClickListener != null) {
                    holder.mainMessageContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnBubbleClickListener.onClick(message);
                        }
                    });
                }

                //Set bubble long click listener
                if (mOnBubbleLongClickListener != null) {
                    holder.mainMessageContainer.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mOnBubbleLongClickListener.onLongClick(message);
                            return true;//ignore onclick event
                        }
                    });
                }
            }

            //Set icon events if icon is shown
            if (message.getIconVisibility() && holder.icon != null) {
                //Set icon click listener
                if (mOnIconClickListener != null) {
                    holder.icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnIconClickListener.onIconClick(message);
                        }
                    });
                }

                if (mOnIconLongClickListener != null) {
                    holder.icon.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mOnIconLongClickListener.onIconLongClick(message);
                            return true;
                        }
                    });
                }
            }

            if(null != holder.messageText) {
                holder.messageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttribute.getMessageFontSize());
                holder.messageText.setMaxWidth(mAttribute.getMessageMaxWidth());
            }
            holder.timeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttribute.getTimeLabelFontSize());
        }

        return convertView;
    }

    /**
     * Add color to drawable
     * @param color setting color
     * @param drawable which be set color
     */
    public void setColorDrawable(int color, Drawable drawable) {
        if (drawable == null) {
            return;
        }
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colorStateList);
    }

    /**
     * Set left bubble background color
     * @param color left bubble color
     */
    public void setLeftBubbleColor(int color) {
        mLeftBubbleColor = color;
        notifyDataSetChanged();
    }

    /**
     * Set right bubble background color
     * @param color right bubble color
     */
    public void setRightBubbleColor(int color) {
        mRightBubbleColor = color;
        notifyDataSetChanged();
    }

    public void setOnIconClickListener(Message.OnIconClickListener onIconClickListener) {
        mOnIconClickListener = onIconClickListener;
    }

    public void setOnBubbleClickListener(Message.OnBubbleClickListener onBubbleClickListener) {
        mOnBubbleClickListener = onBubbleClickListener;
    }

    public void setOnIconLongClickListener(Message.OnIconLongClickListener onIconLongClickListener) {
        mOnIconLongClickListener = onIconLongClickListener;
    }

    public void setOnBubbleLongClickListener(Message.OnBubbleLongClickListener onBubbleLongClickListener) {
        mOnBubbleLongClickListener = onBubbleLongClickListener;
    }

    public void setUsernameTextColor(int usernameTextColor) {
        mUsernameTextColor = usernameTextColor;
        notifyDataSetChanged();
    }

    public void setSendTimeTextColor(int sendTimeTextColor) {
        mSendTimeTextColor = sendTimeTextColor;
        notifyDataSetChanged();
    }

    public void setDateSeparatorColor(int dateSeparatorColor) {
        mDateSeparatorColor = dateSeparatorColor;
        notifyDataSetChanged();
    }

    public void setRightMessageTextColor(int rightMessageTextColor) {
        mRightMessageTextColor = rightMessageTextColor;
        notifyDataSetChanged();
    }

    public void setLeftMessageTextColor(int leftMessageTextColor) {
        mLeftMessageTextColor = leftMessageTextColor;
        notifyDataSetChanged();
    }

    public void setMessageTopMargin(int messageTopMargin) {
        mMessageTopMargin = messageTopMargin;
    }

    public void setMessageBottomMargin(int messageBottomMargin) {
        mMessageBottomMargin = messageBottomMargin;
    }

    public void setStatusColor(int statusTextColor) {
        mStatusColor = statusTextColor;
        notifyDataSetChanged();
    }

    public void setPictureWidth(int width) {
        mLeftPictureWidth = width;
        mRightPictureWidth = width;
    }

    public void setLeftPictureWidth(int width) {
        mLeftPictureWidth = width;
    }

    public void setRightPictureWidth(int width) {
        mRightPictureWidth = width;
    }

    public void setPictureHeight(int height) {
        mLeftPictureHeight = height;
        mRightPictureHeight = height;
    }

    public void setLeftPictureHeight(int height) {
        mLeftPictureHeight = height;
    }

    public void setRightPictureHeight(int height) {
        mRightPictureHeight = height;
    }

    public void setAttribute(Attribute attribute) {
        mAttribute = attribute;
        notifyDataSetChanged();
    }

    class MessageViewHolder {
        CircleImageView icon;
        FrameLayout iconContainer;
        RoundImageView messagePicture;
        TextView messageLink;
        TextView messageText;
        TextView timeText;
        TextView username;
        FrameLayout mainMessageContainer;
        FrameLayout usernameContainer;
        FrameLayout statusContainer;
        ImageView statusIcon;
        TextView statusText;
    }

    class DateViewHolder {
        TextView dateSeparatorText;
    }


}
