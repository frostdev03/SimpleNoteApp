package com.frostdev.mynoteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.frostdev.mynoteapp.db.NoteHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.frostdev.mynoteapp.db.DatabaseContract.NoteColumns.DATE;
import static com.frostdev.mynoteapp.db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.frostdev.mynoteapp.db.DatabaseContract.NoteColumns.TITLE;

public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTitle, edtDescription;
    private boolean isEdit = false;
    private Note note;
    private int position;
    private NoteHelper noteHelper;

    public static final String EXTRA_NOTE = "extra_note"
            ,EXTRA_POSITION =  "extra_position";
    public static final int
            REQUEST_ADD = 100
            ,RESULT_ADD = 101
            ,REQUEST_UPDATE = 200
            ,RESULT_UPDATE = 201
            ,RESULT_DELETE = 301;
    private final int ALERT_DIALOG_CLOSE = 10
            ,ALERT_DIALOG_DELETE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);

        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.edt_desc);
        Button btnSubmit = findViewById(R.id.btn_submit);

        noteHelper = NoteHelper.getInstance(getApplicationContext());
        noteHelper.open();

        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (note != null){
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else {
            note = new Note();
        }

        String actionBarTitle;
        String btnTitle;

        if (isEdit){
            actionBarTitle = "Ubah";
            btnTitle = "Update";

            if (note != null){
                edtTitle.setText(note.getTitle());
                edtDescription.setText(note.getDescription());
            }
        } else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSubmit.setText(btnTitle);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit){
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)){
                edtTitle.setError("Field can not be blank");
                return;
            }

            note.setTitle(title);
            note.setDescription(description);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_NOTE, note);
            intent.putExtra(EXTRA_POSITION, position);

            ContentValues values = new ContentValues();
            values.put(TITLE, title);
            values.put(DESCRIPTION, description);

            if (isEdit){
                long result = noteHelper.update(String.valueOf(note.getId()),values);
                if (result > 0){
                    setResult(RESULT_UPDATE, intent);
                    finish();
                } else {
                    Toast.makeText(NoteAddUpdateActivity.this, "Gagal mengupdate data", Toast.LENGTH_SHORT).show();
                }
            } else {
                note.setDate(getCurrentDate());
                values.put(DATE, getCurrentDate());
                long result = noteHelper.insert(values);

                if (result > 0){
                    note.setId((int) result);
                    setResult(RESULT_ADD, intent);
                    finish();
                } else {
                    Toast.makeText(NoteAddUpdateActivity.this, "Gagal menambah data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit){
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete){
            showAlertDelete(ALERT_DIALOG_DELETE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void showAlertDelete(int type) {
        final boolean isDialogDelete = type == ALERT_DIALOG_DELETE;
        String dialogTitle, dialogMessage;

        if (isDialogDelete) {
            dialogTitle = "Hapus Note";
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";


            AlertDialog.Builder alertDialogDelete = new AlertDialog.Builder(this);

            alertDialogDelete.setTitle(dialogTitle);
            alertDialogDelete.setMessage(dialogMessage)
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, which) -> {
                        if (isDialogDelete) {
                            long result = noteHelper.deleteById(String.valueOf(note.getId()));
                            if (result > 0) {
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_POSITION, position);
                                setResult(RESULT_DELETE, intent);
                                finish();
                            } else {
                                Toast.makeText(NoteAddUpdateActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            }


                        }
                    })
                    .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = alertDialogDelete.create();
            alertDialog.show();
        }
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;

        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(dialogTitle);
            alertDialogBuilder.setMessage(dialogMessage)
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, id) -> {
                        if (isDialogClose) {
                            finish();
                        }
                    })
                    .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}