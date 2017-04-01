package com.landfilleforms.android.landfille_forms.ime;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.landfilleforms.android.landfille_forms.DatePickerFragment;
import com.landfilleforms.android.landfille_forms.R;
import com.landfilleforms.android.landfille_forms.TimePickerFragment;
import com.landfilleforms.android.landfille_forms.database.dao.ImeDao;
import com.landfilleforms.android.landfille_forms.instantaneous.InstantaneousDataFragment;
import com.landfilleforms.android.landfille_forms.model.ImeData;

import java.util.Date;
import java.util.UUID;

//Done
public class ImeDataFragment extends Fragment {
    private static final String ARG_IME_DATA_ID = "ime_data_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_START_TIME = 1;

    private ImeData mImeData;

    private TextView mImeField;
    private TextView mLocationLabel;
    private Spinner mGridIdSpinner;
    private TextView mInspectorLabel;
    private EditText mDescriptionField;
    private EditText mMethaneLevelField;
    private Button mDateButton;
    private Button mStartTimeButton;
    private Button mSubmitButton;



    public static ImeDataFragment newInstance(UUID imeDataId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IME_DATA_ID, imeDataId);
        ImeDataFragment fragment = new ImeDataFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID imeDataId = (UUID) getArguments().getSerializable(ARG_IME_DATA_ID);
        mImeData = ImeDao.get(getActivity()).getImeData(imeDataId);

        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        ImeDao.get(getActivity()).updateImeData(mImeData);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_ime_data, menu);
    }

    //Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO
        switch (item.getItemId()){
            case R.id.menu_item_delete_ime_data:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                dialogDeleteIMEEntry(alertBuilder);
//                ImeDao.get(getActivity()).removeImeData(mImeData);
//                getActivity().finish();
                return true;
           default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ime_data, container, false);


        mInspectorLabel = (TextView)v.findViewById(R.id.inspector_name);
        mInspectorLabel.setText(mImeData.getInspectorFullName());

        mMethaneLevelField = (EditText)v.findViewById(R.id.methane_reading);
        mMethaneLevelField.setText(Double.toString(mImeData.getMethaneReading()));
        mMethaneLevelField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s=="" || count == 0) mImeData.setMethaneReading(0);
                else mImeData.setMethaneReading(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLocationLabel = (TextView) v.findViewById(R.id.location);
        mLocationLabel.setText(mImeData.getLocation());

        mGridIdSpinner = (Spinner)v.findViewById(R.id.grid_id);
        ArrayAdapter<CharSequence> adapter;
        switch(mImeData.getLocation()) {
            case "Bishops":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.bishops_grid, R.layout.dark_spinner_layout);
                break;
            case "Gaffey":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.gaffey_grid, R.layout.dark_spinner_layout);
                break;
            case "Lopez":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.lopez_grid, R.layout.dark_spinner_layout);
                break;
            case "Sheldon":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.sheldon_grid, R.layout.dark_spinner_layout);
                break;
            case "Toyon":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.toyon_grid, R.layout.dark_spinner_layout);
                break;
            default:
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.empty_array, R.layout.dark_spinner_layout);;
        }
        mGridIdSpinner.setAdapter(adapter);
        mGridIdSpinner.setSelection(adapter.getPosition(mImeData.getGridId()));

        mGridIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mImeData.setGridId(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mImeField = (TextView)v.findViewById(R.id.ime_field);
        mImeField.setText(mImeData.getImeNumber());

        mDescriptionField = (EditText)v.findViewById(R.id.description);
        mDescriptionField.setText(mImeData.getDescription());
        mDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mImeData.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button)v.findViewById(R.id.date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mImeData.getDate());
                dialog.setTargetFragment(ImeDataFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mStartTimeButton = (Button)v.findViewById(R.id.start_time);
        updateStartTime();
        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mImeData.getDate());
                dialog.setTargetFragment(ImeDataFragment.this, REQUEST_START_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });


        mSubmitButton = (Button)v.findViewById(R.id.submit);
        mSubmitButton.setText(R.string.submit_button_label);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(mImeData.getMethaneReading() < 500){
                    Toast.makeText(getActivity(), R.string.improper_methane_ime_toast, Toast.LENGTH_SHORT).show();
                } else {
                    getActivity().finish();
                    Toast.makeText(getActivity(), R.string.ime_added_toast, Toast.LENGTH_SHORT).show();
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
            mImeData.setDate(date);
            updateDate();
            updateStartTime();
        }

        else if (requestCode == REQUEST_START_TIME) {//Accidentally setting this as (resultCode == REQUEST_DATE) cost me 30 min of debug time orz
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mImeData.setDate(date);
            updateStartTime();
        }

    }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("EEEE, MMM d, yyyy",mImeData.getDate()));
    }

    private void updateStartTime() {
        mStartTimeButton.setText(DateFormat.format("HH:mm",mImeData.getDate()));
    }

    private void dialogDeleteIMEEntry(AlertDialog.Builder alertBuilder) {
        alertBuilder.setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ImeDao.get(getActivity()).removeImeData(mImeData);
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
        deleteAlert.setTitle("Delete IME Entry");
        deleteAlert.show();
    }


}
