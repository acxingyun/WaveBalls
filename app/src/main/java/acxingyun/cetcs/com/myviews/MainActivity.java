package acxingyun.cetcs.com.myviews;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private WaitingBallView waitingBallView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waitingBallView = findViewById(R.id.waitingBallView);
        waitingBallView.startAnimation();
    }
}
