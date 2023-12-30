package morgan.jones.whatistwitter;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment
{
    private View staticView;
    private View fragView;

    private DataManager dm;

    private CardView aboutCard;
    private TextView aboutText;
    private CardView addNewCard;
    private TextView addNewText;
    private EditText newCatName;
    private CardView assignUsers;

    private boolean expanded;
    private boolean addNewExpand;
    private boolean inputValid;

    private ViewGroup.LayoutParams params;
    private ViewGroup.LayoutParams paramsAddNew;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (staticView == null)
        {
            fragView = inflater.inflate(R.layout.settings_fragment, container, false);
            staticView = fragView;
        }
        else
        {
            fragView = staticView;
        }

        dm = new DataManager();
        FileReader.readData(staticView.getContext(), dm);

        aboutCard = fragView.findViewById(R.id.settings_about);
        aboutText = fragView.findViewById(R.id.about_info);
        addNewCard = fragView.findViewById(R.id.settings_add_category);
        addNewText = fragView.findViewById(R.id.add_new_text);
        newCatName = fragView.findViewById(R.id.new_cat_name);
        assignUsers = fragView.findViewById(R.id.settings_assign_users);

        setupExpand();
        setupAddNew();

        assignUsers.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment fragment = new AssignUsersFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_placeholder,
                        fragment).commitAllowingStateLoss();
                HomeActivity.staticFragment = fragment;
            }
        });

        return fragView;
    }

    private void setupAddNew()
    {
        paramsAddNew = addNewCard.getLayoutParams();
        addNewExpand = false;
        paramsAddNew.height = 184;
        addNewCard.setLayoutParams(paramsAddNew);
        inputValid = false;
        addNewText.setEnabled(true);
        addNewText.bringToFront();
        addNewText.setClickable(true);

        newCatName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (newCatName.getText().toString().length() > 3)
                {
                    addNewText.setTextColor(Color.GREEN);
                    inputValid = true;
                }
                else
                {
                    addNewText.setTextColor(Color.GRAY);
                    inputValid = false;
                }

                for (Category c : dm.getCategories())
                {
                    if (c.getName().toLowerCase().equals(newCatName.getText().toString().
                            toLowerCase()))
                    {
                        inputValid = false;
                        addNewText.setTextColor(Color.GRAY);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        addNewText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (inputValid)
                {
                    dm.getCategories().add(new Category(newCatName.getText().toString()));
                    FileReader.writeData(getContext(), dm);
                    Toast.makeText(getContext(), "Category Added.", Toast.LENGTH_SHORT).
                            show();
                    addNewText.setTextColor(Color.parseColor("#b3b3b3"));
                    despand();
                }
                else
                {
                    Toast.makeText(getContext(), "Enter valid data. Unique and >3 characters"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        addNewCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!addNewExpand)
                {
                    paramsAddNew.height = 250;
                    newCatName.setEnabled(true);
                    newCatName.setVisibility(View.VISIBLE);
                    addNewExpand = true;
                    newCatName.requestFocus();
                }
                else
                {
                    despand();
                }

                addNewCard.setLayoutParams(paramsAddNew);
            }
        });
    }

    private void despand()
    {
        paramsAddNew.height = 184;
        newCatName.setEnabled(false);
        newCatName.setVisibility(View.INVISIBLE);
        addNewExpand = false;
        addNewText.setTextColor(Color.GRAY);
        inputValid = false;
        newCatName.setText("");
    }

    private void setupExpand()
    {
        params = aboutCard.getLayoutParams();

        // Original
        params.height = 184;
        aboutCard.setLayoutParams(params);
        aboutText.setEnabled(false);
        aboutText.setVisibility(View.INVISIBLE);
        expanded = false;

        aboutCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!expanded)
                {
                    params.height = 1750;
                    aboutText.setEnabled(true);
                    aboutText.setVisibility(View.VISIBLE);
                    expanded = true;
                }
                else
                {
                    params.height = 184;
                    aboutText.setEnabled(false);
                    aboutText.setVisibility(View.INVISIBLE);
                    expanded = false;
                }

                aboutCard.setLayoutParams(params);
            }
        });
    }
}
