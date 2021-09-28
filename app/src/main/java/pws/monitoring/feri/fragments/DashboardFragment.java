package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.adapters.RecipientAdapter;
import pws.monitoring.feri.events.OnRecipientModify;
import pws.monitoring.feri.events.OnRecipientShow;


public class DashboardFragment extends Fragment {
    private RecyclerView recipientsRecyclerView;
    private RecipientAdapter recipientAdapter;

    Button buttonSearch;
    Button buttonCalendar;
    Button buttonAdd;

    User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_dashboard, container, false);

        bindGUI(rootView);
        bindValues();

        user = ApplicationState.loadLoggedUser();

        return rootView;
    }

    private void bindGUI(ViewGroup v) {
        recipientsRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewRecipients);
        buttonSearch = (Button) v.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //modal
            }
        });
        buttonCalendar = (Button) v.findViewById(R.id.buttonCalendar);
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open calendar fragment
            }
        });
        buttonAdd = (Button) v.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //QR code scanner
            }
        });
    }


    private void bindValues() {
        recipientAdapter = new RecipientAdapter(requireContext(), user.getRecipients());
        recipientsRecyclerView.setAdapter(recipientAdapter);
        recipientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnRecipientShow event) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        RecipientDetailsFragment recipientFragment = new RecipientDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("recipient", ApplicationState.getGson().toJson(event.getRecipient()));
        recipientFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_navigation, recipientFragment).commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnRecipientModify event) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        RecipientModifyFragment recipientFragment = new RecipientModifyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("recipient", ApplicationState.getGson().toJson(event.getRecipient()));
        recipientFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_navigation, recipientFragment).commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}