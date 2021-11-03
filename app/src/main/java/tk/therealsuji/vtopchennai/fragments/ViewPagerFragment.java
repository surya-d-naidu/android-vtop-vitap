package tk.therealsuji.vtopchennai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.TooltipCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tk.therealsuji.vtopchennai.R;
import tk.therealsuji.vtopchennai.activities.MainActivity;
import tk.therealsuji.vtopchennai.adapters.StaffAdapter;
import tk.therealsuji.vtopchennai.helpers.AppDatabase;
import tk.therealsuji.vtopchennai.interfaces.StaffDao;

public class ViewPagerFragment extends Fragment {
    public static final int TYPE_COURSES = 1;
    public static final int TYPE_STAFF = 2;

    AppDatabase appDatabase;
    TabLayout tabLayout;
    ViewPager2 viewPager;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    private void attachStaff() {
        StaffDao staffDao = this.appDatabase.staffDao();
        float pixelDensity = this.getResources().getDisplayMetrics().density;

        staffDao
                .getStaffTypes()
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull List<String> staffTypes) {
                        viewPager.setAdapter(new StaffAdapter(staffTypes));

                        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                            tab.setText(staffTypes.get(position));
                            TooltipCompat.setTooltipText(tab.view, null);
                        }).attach();

                        for (int i = 0; i < tabLayout.getTabCount(); ++i) {
                            View day = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                            ViewGroup.MarginLayoutParams tabParams = (ViewGroup.MarginLayoutParams) day.getLayoutParams();

                            if (i == 0) {
                                tabParams.setMarginStart((int) (20 * pixelDensity));
                                tabParams.setMarginEnd((int) (5 * pixelDensity));
                            } else if (i == tabLayout.getTabCount() - 1) {
                                tabParams.setMarginStart((int) (5 * pixelDensity));
                                tabParams.setMarginEnd((int) (20 * pixelDensity));
                            } else {
                                tabParams.setMarginStart((int) (5 * pixelDensity));
                                tabParams.setMarginEnd((int) (5 * pixelDensity));
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                });
    }

    private void attachCourses() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewPagerFragment = inflater.inflate(R.layout.fragment_view_pager, container, false);

        LinearLayout header = viewPagerFragment.findViewById(R.id.header);
        header.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            layoutParams.setMargins(0, windowInsets.getSystemWindowInsetTop(), 0, 0);
            view.setLayoutParams(layoutParams);

            return windowInsets.consumeSystemWindowInsets();
        });

        this.appDatabase = AppDatabase.getInstance(this.requireActivity().getApplicationContext());
        this.tabLayout = viewPagerFragment.findViewById(R.id.tab_layout);
        this.viewPager = viewPagerFragment.findViewById(R.id.view_pager);

        int titleId = 0, contentType = 0;
        Bundle arguments = this.getArguments();

        if (arguments != null) {
            titleId = arguments.getInt("title_id", 0);
            contentType = arguments.getInt("content_type", 0);
        }

        viewPagerFragment.findViewById(R.id.button_back).setOnClickListener(view -> requireActivity().getSupportFragmentManager().popBackStack());
        ((TextView) viewPagerFragment.findViewById(R.id.text_title)).setText(getString(titleId));

        switch (contentType) {
            case TYPE_COURSES:
                this.attachCourses();
                break;
            case TYPE_STAFF:
                this.attachStaff();
                break;
        }

        return viewPagerFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ((MainActivity) this.requireActivity()).showBottomNavigationView();
    }
}