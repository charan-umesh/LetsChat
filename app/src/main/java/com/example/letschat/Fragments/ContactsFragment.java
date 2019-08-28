package com.example.letschat.Fragments;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.core.app.Fragment;
import androidx.core.app.LoaderManager.LoaderCallbacks;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.letschat.R;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };

    private final static int[] TO_IDS = {
            android.R.id.text1
    };

    ListView contactsList;

    long contactId;

    String contactKey;

    Uri contactUri;

    private SimpleCursorAdapter cursorAdapter;

    public ContactsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_list_fragment, container, false);
    }

    @SuppressLint("ResourceType")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsList = (ListView) getActivity().findViewById(R.layout.contact_list_view);
        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.contact_list_item, null, FROM_COLUMNS, TO_IDS, 0);
        contactsList.setAdapter(cursorAdapter);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        contactsList.setOnItemClickListener(this);
    }

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };

    private static final int CONTACT_ID_INDEX = 0;

    private static final int CONTACT_KEY_INDEX = 1;

    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";

    private String searchString;

    private String[] selectionArgs = {searchString};

    @Override
    public void onItemClick(
            AdapterView<?> parent, View item, int position, long rowID) {

        Cursor cursor = parent.getAdapter().getCursor();

        cursor.moveToPosition(position);

        contactId = cursor.getLong(CONTACT_ID_INDEX);

        contactKey = cursor.getString(CONTACT_KEY_INDEX);

        contactUri = ContactsContract.Contacts.getLookupUri(contactId, mContactKey);

    }

    public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getLoaderManager().initLoader(0, null, this);

            @Override
            public Loader<Cursor> onCreateLoader ( int loaderId, Bundle args){
                selectionArgs[0] = "%" + searchString + "%";
                return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, selectionArgs, null);
            }

            @Override
            public void onLoadFinished (Loader < Cursor > loader, Cursor cursor){
                cursorAdapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset (Loader < Cursor > loader) {

                cursorAdapter.swapCursor(null);

            }

            @SuppressLint("InlinedApi")
            private static final String[] PROJECTION =
                    {
                            ContactsContract.Data._ID,

                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                                    ContactsContract.Data.DISPLAY_NAME_PRIMARY :
                                    ContactsContract.Data.DISPLAY_NAME,

                            ContactsContract.Data.CONTACT_ID,

                            ContactsContract.Data.LOOKUP_KEY
                    };
            private static final String SELECTION =

                    ContactsContract.CommonDataKinds.Email.ADDRESS + " LIKE ? " + "AND " +

                            ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";
            String searchString;
            String[] selectionArgs = {""};
            @Override
            public Loader<Cursor> onCreateLoader ( int loaderId, Bundle args){

                searchString = "%" + searchString + "%";

                selectionArgs[0] = searchString;

                return new CursorLoader(getActivity(), ContactsContract.Data.CONTENT_URI, PROJECTION, SELECTION, selectionArgs, null);
            }
            @Override
            public Loader<Cursor> onCreateLoader ( int loaderId, Bundle args){
                /*
                 * Appends the search string to the base URI. Always
                 * encode search strings to ensure they're in proper
                 * format.
                 */
                Uri contentUri = Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_FILTER_URI,
                        Uri.encode(searchString));
                // Starts the query
                return new CursorLoader(getActivity(), contentUri, PROJECTION, null, null, null);
            }
        }
    }
}
