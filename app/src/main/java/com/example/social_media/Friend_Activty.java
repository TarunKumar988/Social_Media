package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.social_media.Fragments.AddFragment;
import com.example.social_media.Fragments.FirendRequestFragment;
import com.example.social_media.Fragments.FriendsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Friend_Activty extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView textView;
    CircleImageView profile;
    ViewPagerAdapter viewPagerAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String name,image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend__activty);
        tabLayout=findViewById(R.id.tablayout);
        viewPager=findViewById(R.id.viewpager);
        textView=findViewById(R.id.name);

        profile=findViewById(R.id.profile);

        sharedPreferences=getSharedPreferences("SIGN_IN",MODE_PRIVATE);
        name=sharedPreferences.getString("Name",null);
        image=sharedPreferences.getString("Image",null);
        textView.setText(name);

        Glide.with(this).load(image).into(profile);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragement(new AddFragment(),"Add Friend");
        viewPagerAdapter.addFragement(new FirendRequestFragment(),"Requets");
        viewPagerAdapter.addFragement(new FriendsFragment(),"Friend");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
class ViewPagerAdapter extends FragmentPagerAdapter
{
    ArrayList<String> titles;
    ArrayList<Fragment> fragments;
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments=new ArrayList<>();
        this.titles=new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    public void addFragement(Fragment frag, String title)
    {

        fragments.add(frag);
        titles.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


    private void setData()
    {
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();


    }
}
