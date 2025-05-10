package com.www.avia.kg.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.www.avia.kg.app.R;
import com.www.avia.kg.app.databinding.TaFragmentInfoBinding;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {

    private TaFragmentInfoBinding binding;
    private InfoAdapter adapter;
    private final List<InfoItem> infoItemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TaFragmentInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up RecyclerView
        adapter = new InfoAdapter(infoItemList, new InfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InfoItem item) {
                // Handle navigation on item click
                NavController navController = Navigation.findNavController(view);
                Bundle args = new Bundle();
                args.putString("item_name", item.title); // Pass arguments if needed
                //navController.navigate(R.id.destinationFragment, args); // Navigate to desired destination
            }
        });
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.list.setAdapter(adapter);

        // Dummy data
        loadData();
    }

    private void loadData() {
        infoItemList.add(new InfoItem(R.drawable.ic_16_push_notification, "Item 1"));
        infoItemList.add(new InfoItem(R.drawable.ic_16_push_notification, "Item 2"));
        infoItemList.add(new InfoItem(R.drawable.ic_16_push_notification, "Item 3"));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Avoid memory leaks
    }
}
