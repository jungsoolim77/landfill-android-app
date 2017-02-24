package com.landfilleforms.android.landfille_forms.instantaneous;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.landfilleforms.android.landfille_forms.DatePickerFragment;
import com.landfilleforms.android.landfille_forms.R;
import com.landfilleforms.android.landfille_forms.SessionManager;
import com.landfilleforms.android.landfille_forms.TimePickerFragment;
import com.landfilleforms.android.landfille_forms.ime.ImeDataPagerActivity;
import com.landfilleforms.android.landfille_forms.ime.ImeForm;
import com.landfilleforms.android.landfille_forms.ime.ImeFormActivity;
import com.landfilleforms.android.landfille_forms.model.ImeData;
import com.landfilleforms.android.landfille_forms.model.InstantaneousData;
import com.landfilleforms.android.landfille_forms.model.User;
import com.landfilleforms.android.landfille_forms.model.WarmSpotData;
import com.landfilleforms.android.landfille_forms.warmspot.WarmSpotDataPagerActivity;
import com.landfilleforms.android.landfille_forms.warmspot.WarmSpotForm;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Work on 11/4/2016.
 */

public class InstantaneousDataFragment extends Fragment {
    private static final String ARG_INSTANTANEOUS_DATA_ID = "instantaneous_data_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_START_TIME = 1;
    private static final int REQUEST_END_TIME = 2;

    private InstantaneousData mInstantaneousData;
    private TextView mInspectorLabel;
    private EditText mGridIdField;
    private EditText mMethaneLevelField;
    private EditText mBaroLevelField;
    private Button mStartDateButton;
    private Button mStartTimeButton;
    private Button mEndTimeButton;
    private Button mSubmitButton;
    private EditText mInstrumentField;
    private EditText imeField;
    private TextView mLocationLabel;


    //chris added this
    private User mUser;
    private SessionManager session;

    public static InstantaneousDataFragment newInstance(UUID instantaneousDataId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_INSTANTANEOUS_DATA_ID, instantaneousDataId);
        InstantaneousDataFragment fragment = new InstantaneousDataFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID instantaneousDataId = (UUID) getArguments().getSerializable(ARG_INSTANTANEOUS_DATA_ID);
        mInstantaneousData = InstantaneousForm.get(getActivity()).getInstantaneousData(instantaneousDataId);

        setHasOptionsMenu(true);

        //chris added this
        session = new SessionManager(getActivity().getApplicationContext());
        session.checkLogin();

        HashMap<String,String> currentUser = session.getUserDetails();
        mUser = new User();
        mUser.setUsername(currentUser.get(SessionManager.KEY_USERNAME));
        Log.d("UserName:", mUser.getUsername());
        mUser.setFullName(currentUser.get(SessionManager.KEY_USERFULLNAME));
    }

    @Override
    public void onPause() {
        super.onPause();

        InstantaneousForm.get(getActivity()).updateInstantanteousData(mInstantaneousData);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_instantaneous_data, menu);
    }

    //Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_instantaneous_data:
                //chris modified this
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                dialogDeleteInstantaneousEntry(alertBuilder);
//                InstantaneousForm.get(getActivity()).removeInstantaneousData(mInstantaneousData);
//                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instantaneous_data, container, false);

        mBaroLevelField = (EditText)v.findViewById(R.id.baro_reading);
        mBaroLevelField.setText(Double.toString(mInstantaneousData.getBarometricPressure()));
        mBaroLevelField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s=="" || count == 0) mInstantaneousData.setBarometricPressure(0);
                else mInstantaneousData.setBarometricPressure(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mInspectorLabel = (TextView)v.findViewById(R.id.inspector_name);
        mInspectorLabel.setText(mInstantaneousData.getInspectorName());

        mMethaneLevelField = (EditText)v.findViewById(R.id.methane_reading);
        mMethaneLevelField.setText(Double.toString(mInstantaneousData.getMethaneReading()));
        mMethaneLevelField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s=="" || count == 0) mInstantaneousData.setMethaneReading(0);
                else mInstantaneousData.setMethaneReading(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLocationLabel = (TextView) v.findViewById(R.id.location);
        mLocationLabel.setText(mInstantaneousData.getLandFillLocation());
        
        mGridIdField = (EditText)v.findViewById(R.id.grid_id);
        mGridIdField.setText(mInstantaneousData.getGridId());
        mGridIdField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mInstantaneousData.setGridId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mInstrumentField = (EditText)v.findViewById(R.id.instrument_field);
//        mInstrumentField.setText(mInstantaneousData.getInstrumentSerialNumber());
//        mInstrumentField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s=="" || count == 0) mInstantaneousData.setInstrumentSerialNumber("");
//                else mInstantaneousData.setInstrumentSerialNumber(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        imeField = (EditText)v.findViewById(R.id.ime_field);
        imeField.setText(mInstantaneousData.getImeNumber());
        imeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mInstantaneousData.setImeNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mStartDateButton = (Button)v.findViewById(R.id.start_date);
//        updateDate();
//        mStartDateButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                FragmentManager manager = getActivity().getSupportFragmentManager();
//                DatePickerFragment dialog = DatePickerFragment.newInstance(mInstantaneousData.getStartDate());
//                dialog.setTargetFragment(InstantaneousDataFragment.this, REQUEST_DATE);
//                dialog.show(manager, DIALOG_DATE);
//            }
//        });

//        mStartTimeButton = (Button)v.findViewById(R.id.start_time);
//        updateStartTime();
//        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                FragmentManager manager = getActivity().getSupportFragmentManager();
//                TimePickerFragment dialog = TimePickerFragment.newInstance(mInstantaneousData.getStartDate());
//                dialog.setTargetFragment(InstantaneousDataFragment.this, REQUEST_START_TIME);
//                dialog.show(manager, DIALOG_TIME);
//            }
//        });

//        mEndTimeButton = (Button)v.findViewById(R.id.end_time);
//        updateEndTime();
//        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                FragmentManager manager = getActivity().getSupportFragmentManager();
//                TimePickerFragment dialog = TimePickerFragment.newInstance(mInstantaneousData.getEndDate());
//                dialog.setTargetFragment(InstantaneousDataFragment.this, REQUEST_END_TIME);
//                dialog.show(manager, DIALOG_TIME);
//            }
//        });

        mSubmitButton = (Button)v.findViewById(R.id.submit);
        mSubmitButton.setText(R.string.submit_button_label);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //create a new alert dialog
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                //System.out.println(mInstantaneousData.getMethaneReading());
                //case where ch4 is over 500, indicated as an IME
                if (mInstantaneousData.getMethaneReading() >= 500) {
                    dialogIME(alertBuilder);
                }
                //case where ch4 is between 200 and 499, indicated as a Warmspot
                else if (mInstantaneousData.getMethaneReading() >= 200 && mInstantaneousData.getMethaneReading() <=499) {
                    dialogWarmspot(alertBuilder);
                }
                else{
                    //use this after all other ones are working
                    getActivity().finish();
                    //unsure about implementing text after submitting for instantaneous form
                    //Toast.makeText(getActivity(), R.string.instantaneous_added_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {//Accidentally setting this as (resultCode == REQUEST_DATE) cost me 30 min of debug time orz
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mInstantaneousData.setStartDate(date);
            mInstantaneousData.setEndDate(date);
            updateDate();
            updateStartTime();
            updateEndTime();
        }

        else if (requestCode == REQUEST_START_TIME) {//Accidentally setting this as (resultCode == REQUEST_DATE) cost me 30 min of debug time orz
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mInstantaneousData.setStartDate(date);
            updateStartTime();
        }

        else if (requestCode == REQUEST_END_TIME) {//Accidentally setting this as (resultCode == REQUEST_DATE) cost me 30 min of debug time orz
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mInstantaneousData.setEndDate(date);
            updateEndTime();
        }
    }

    private void updateDate() {
        mStartDateButton.setText(DateFormat.format("EEEE, MMM d, yyyy",mInstantaneousData.getStartDate()));
    }

    private void updateStartTime() {
        mStartTimeButton.setText(DateFormat.format("HH:mm",mInstantaneousData.getStartDate()));
    }

    private void updateEndTime() {
        mEndTimeButton.setText(DateFormat.format("HH:mm",mInstantaneousData.getEndDate()));
    }

    //dialog when there is an IME
    private void dialogIME(AlertDialog.Builder alertBuilder) {
        final AlertDialog.Builder redirectionAlert = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("CH4 levels are over 500! There is an IME! Navigate to IME form?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //comment this out temp
                //getActivity().finish();
                dialogIMENavigation(redirectionAlert);
                InstantaneousForm.get(getActivity()).updateInstantanteousData(mInstantaneousData);
                //later, need to redirect to appropriate location's IME list
                //Toast.makeText(getActivity(), R.string.coming_soon_toast, Toast.LENGTH_SHORT).show();

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.setTitle("New IME");
        alert.show();
    }

    //dialog to create a new IME form or add to an existing IME
    private void dialogIMENavigation(AlertDialog.Builder redirectionAlert) {
        redirectionAlert.setMessage("Would you like to create a new IME or add to an existing one?")
                //set positive as "Add to Existing IME",
                .setCancelable(false).setPositiveButton("Add to Existing IME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //add extisting, put them into list
                Intent getImeFormActivity = new Intent(getActivity(), ImeFormActivity.class);
                //temp just put lopez
                String tempLocation = null;
                getImeFormActivity.putExtra(tempLocation, "Lopez");
                startActivity(getImeFormActivity);
                getActivity().finish();
            }//temp fix, set this to cancel to rearrange order of options
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNegativeButton("Create a new IME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //make a new IME
                ImeData imeData = new ImeData();
                imeData.setLocation("Lopez");
                imeData.setInspectorFullName(mUser.getFullName());
                imeData.setInspectorUserName(mUser.getUsername());
                ImeForm.get(getActivity()).addImeData(imeData);
                Intent navigateIMEForm = ImeDataPagerActivity.newIntent(getActivity(),imeData.getId());
                startActivity(navigateIMEForm);
                getActivity().finish();
            }
        });
        AlertDialog alert = redirectionAlert.create();
        alert.setTitle("IME Navigation");
        alert.show();

    }

    private void dialogWarmspot(AlertDialog.Builder alertBuilder) {
        alertBuilder.setMessage("CH4 levels are between 200-499! There is a Warmspot. Navigate to Warmspot form?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //later, need to redirect to appropriate location's IME list
                // Toast.makeText(getActivity(), R.string.coming_soon_toast, Toast.LENGTH_SHORT).show();
                WarmSpotData warmSpotData = new WarmSpotData();
                //Log.d("From FormFrag",getActivity().getIntent().getStringExtra(EXTRA_USERNAME));
                //temp default is lopez
                warmSpotData.setLocation("Lopez");
                warmSpotData.setInspectorFullName(mUser.getFullName());
                warmSpotData.setInspectorUserName(mUser.getUsername());
                WarmSpotForm.get(getActivity()).addWarmSpotData(warmSpotData);
                //still need to pass through some data between warmspots
                Intent navigateWarmspotForm = WarmSpotDataPagerActivity.newIntent(getActivity(),warmSpotData.getId());
                startActivity(navigateWarmspotForm);
                getActivity().finish();
                //note: add this one to the warmspot form.
               // Toast.makeText(getActivity(), R.string.warmspot_added_toast, Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.setTitle("New Warmspot");
        alert.show();

    }


    private void dialogDeleteInstantaneousEntry(AlertDialog.Builder alertBuilder) {
        alertBuilder.setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InstantaneousForm.get(getActivity()).removeInstantaneousData(mInstantaneousData);
                        getActivity().finish();
                        Toast.makeText(getActivity(), R.string.entry_deleted_toast, Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog deleteAlert = alertBuilder.create();
        deleteAlert.setTitle("Delete Instantaneous Entry");
        deleteAlert.show();
    }
}
