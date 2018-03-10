package ncc4702.hpu.edu.usszhum;


import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityFragment extends Fragment {

    private CannonView cannonView;//Custom view to display the game

//    public MainActivityFragment() {
//        // Required empty public constructor
//    }


    //called when Fragment's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater,container,savedInstanceState);

        //inflate the fragment_main.cml layout
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //get a reference to the CannonView
        cannonView = (CannonView) view.findViewById(R.id.cannonView);

        return view;
    }

    //set up volume control once Activity is created
    @Override
    public void onActivityCreated(Bundle savedInstancState){
        super.onActivityCreated(savedInstancState);

        //allow volume buttons to set game volume
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    //when MainActivity is paused, terminate the game
    @Override
    public void onPause(){
        super.onPause();
        cannonView.stopGame();//terminates the game found in 6.13
    }

    //when MainActivity is paused, MainActivityFragment releases resources
    @Override
    public void onDestroy(){
        super.onDestroy();
        cannonView.releaseResources();//found also in 6.13
    }

}
