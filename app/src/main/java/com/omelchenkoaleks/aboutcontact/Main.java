package com.omelchenkoaleks.aboutcontact;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity {
    private static final int REQ_GET_CONTACT = 1;
    private EditText number;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        number = (EditText) findViewById(R.id.contactText);

        Button b = (Button) findViewById(R.id.contactChoose);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Uri uri = ContactsContract.Contacts.CONTENT_URI;
                System.out.println(uri);
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                startActivityForResult(intent, REQ_GET_CONTACT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_GET_CONTACT) {
            switch(resultCode) {
                case Activity.RESULT_OK:
                    // The Contacts API is about the most complex to use.
                    // First we have to retrieve the Contact, since we only get its URI from the Intent
                    Uri resultUri = data.getData(); // e.g., content://contacts/people/123
                    Cursor cont = getContentResolver().query(resultUri, null, null, null, null);
                    if (!cont.moveToNext()) {	// expect 001 row(s)
                        Toast.makeText(this, "Cursor contains no data", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int columnIndexForId = cont.getColumnIndex(ContactsContract.Contacts._ID);
                    String contactId = cont.getString(columnIndexForId);
                    int columnIndexForHasPhone = cont.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    boolean hasAnyPhone = cont.getInt(columnIndexForHasPhone) != 0;
                    if (!hasAnyPhone) {
                        Toast.makeText(this, "Selected contact seems to have no phone numbers ", Toast.LENGTH_LONG).show();
                    }
                    // Now we have to do another query to actually get the numbers!
                    Cursor numbers = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, // "selection",
                            null, null);
                    // XXX still need to restrict to Mobile number!
                    while (numbers.moveToNext()) {
                        String aNumber = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        System.out.println(aNumber);
                        number.setText(aNumber);
                    }
                    if (cont.moveToNext()) {
                        System.out.println("WARNING: More than one contact returned from picker!");
                    }
                    numbers.close();
                    cont.close();
                    break;
                case Activity.RESULT_CANCELED:
                    // nothing to do here
                    break;
                default:
                    Toast.makeText(this, "Unexpected resultCode: " + resultCode, Toast.LENGTH_LONG).show();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return false;
    }
}