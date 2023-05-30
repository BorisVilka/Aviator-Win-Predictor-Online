package winpredictor.game.onlinepred

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import winpredictor.game.onlinepred.databinding.FragmentGameBinding


class FragmentGame : Fragment() {

    private lateinit var binding:FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater,container,false)
        val activity = requireActivity()
        val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
        binding.gameView.setEnd {
            activity.runOnUiThread {
                Toast.makeText(activity,"You lose", Toast.LENGTH_LONG).show()
                binding.gameView.togglePause()
                navController.popBackStack()
            }
        }
        binding.imageView8.setOnClickListener {
            if(binding.gameView.paused) {
                binding.imageView8.setImageResource(R.drawable.ic_baseline_pause_24)
            } else {
                binding.imageView8.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
            binding.gameView.togglePause()
        }
        binding.imageView7.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
        }
        return binding.root
    }


}