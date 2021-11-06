package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.adapters.RecipientAdapter;
import pws.monitoring.feri.events.OnFragmentChanged;
import pws.monitoring.feri.events.OnRecipientModify;
import pws.monitoring.feri.events.OnRecipientShow;


public class DashboardFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabCollectionAdapter tabCollectionAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().post(new OnFragmentChanged(false));

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_dashboard, container, false);

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabCollectionAdapter = new TabCollectionAdapter(this);
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(tabCollectionAdapter);
        tabLayout = view.findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if(position == 0) {
                tab.setText(requireActivity().getResources().getString(R.string.title_recipients));
            } else {
                tab.setText(requireActivity().getResources().getString(R.string.title_history));
            }
        }).attach();
    }

    public class TabCollectionAdapter extends FragmentStateAdapter {
        public TabCollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 0)
                return new RecipientListFragment();
            else
                return new CalendarFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}