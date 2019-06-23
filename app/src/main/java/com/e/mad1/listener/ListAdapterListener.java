package com.e.mad1.listener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.e.mad1.App;
import com.e.mad1.DataRepository;
import com.e.mad1.R;
import com.e.mad1.database.Entity.EventEntity;
import com.e.mad1.ui.ScheduleEventActivity;
import com.google.android.material.snackbar.Snackbar;

public class ListAdapterListener {
    private Context context;
    public ListAdapterListener(Context context) {
        this.context = context;
    }
    public OpenEditEvent setOpenEditEvent(long eventId){
        return new OpenEditEvent(eventId);
    }
    public DeleteEventButton setDeleteEvent(ConstraintLayout eventView,EventEntity event){
        return new DeleteEventButton(eventView,event);
    }

    public class OpenEditEvent implements View.OnClickListener{
        private long eventId;
        OpenEditEvent(long eventId){
            this.eventId = eventId;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ScheduleEventActivity.class);
            intent.putExtra("com.e.mad1.UI",eventId);
            context.startActivity(intent);
        }
    }

    public class DeleteEventButton implements ImageButton.OnClickListener{
        private boolean proceedDelete = true;
        private ConstraintLayout eventView;
        private EventEntity event;

        DeleteEventButton(ConstraintLayout eventView, EventEntity event){
            this.eventView = eventView;
            this.event = event;
        }

        @Override
        public void onClick(View view) {
            String name = event.getTitle();

            // set visibility to GONE and height to 0 to hide the Cell
            eventView.setVisibility(View.GONE);
            eventView.setMaxHeight(0);
            Snackbar snackbar = Snackbar.make(eventView, name + " removed", Snackbar.LENGTH_LONG);
            snackbar.setAction(context.getResources().getString(R.string.undo), v -> {  // undo for user feel regret deleting
                proceedDelete = false;
                // if undo, set visibility to Visible and set the height back to default to show
                eventView.setVisibility(View.VISIBLE);
                eventView.setMaxHeight(Integer.MAX_VALUE);
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event1) {
                    // delete event for real if user not undo
                    if (proceedDelete){ //if not press undo, delete from db
                        DataRepository repository = ((App) context.getApplicationContext()).getRepository();
                        repository.deleteEventById(event.getId());
                    } else proceedDelete = true;
                }
            });
            snackbar.show();
        }
    }
}
