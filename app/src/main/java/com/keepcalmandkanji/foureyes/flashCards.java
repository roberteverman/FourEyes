package com.keepcalmandkanji.foureyes;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class flashCards extends AppCompatActivity {

  FrameLayout mainFrame;
  private AnimatorSet mSetOut;
  private AnimatorSet mSetIn;
  private AnimatorSet mSetTopOut;
  private AnimatorSet mSetTopIn;
  private AnimatorSet mSetBottomOut;
  private AnimatorSet mSetBottomIn;
  private AnimatorSet mSetLeftOut;
  private AnimatorSet mSetLeftIn;
  private View mCardFrontLayout;
  private View mCardBackLayout;
  private View mCardTopLayout;
  private View mCardBottomLayout;
  private boolean topSwiped = false;
  private boolean bottomSwiped = false;
  private boolean tapInitiated = false;
  private boolean initatedFromBack = false;
  private boolean mIsBackVisible = false;
  private boolean mIsTopVisible = false;
  private boolean mIsFrontVisible = true;
  private boolean mIsBottomVisible = false;
  DatabaseAccess databaseAccess;
  int flashcardSize;
  int counter;
  int[] positionNumbers;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_flash_cards);

    //set up database
    databaseAccess = DatabaseAccess.getInstance(this);
    databaseAccess.open();

    //Get TextViews
    TextView frontText = (TextView) findViewById(R.id.frontText);
    TextView backText = (TextView) findViewById(R.id.backText);
    TextView topText = (TextView) findViewById(R.id.topText);
    TextView bottomText = (TextView) findViewById(R.id.bottomText);
    TextView leftText = (TextView) findViewById(R.id.leftText);
    TextView rightText = (TextView) findViewById(R.id.rightText);

    //get data from main activity
    String selectedTable = getIntent().getStringExtra("selectedTable");
    String selectedFront = getIntent().getStringExtra("selectedFront");
    String selectedBack = getIntent().getStringExtra("selectedBack");
    String selectedTop = getIntent().getStringExtra("selectedTop");
    String selectedBottom = getIntent().getStringExtra("selectedBottom");

    positionNumbers = getIntent().getIntArrayExtra("positionNumbers");
    counter = 0;
    flashcardSize = positionNumbers.length;

    //set initial card text
    frontText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getCurrentPosition()));
    backText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedBack,getCurrentPosition()));
    topText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedTop,getCurrentPosition()));
    bottomText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedBottom,getCurrentPosition()));
    leftText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getLeftPosition()));
    rightText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getRightPosition()));

    //load stuff
    findViews();
    loadAnimations();
    changeCameraDistance();

    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    int height = displayMetrics.heightPixels;
    final int width = displayMetrics.widthPixels;

    final FrameLayout mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
    final FrameLayout backCard = (FrameLayout) findViewById(R.id.card_back);
    final FrameLayout frontCard = (FrameLayout) findViewById(R.id.card_front);
    final FrameLayout topCard = (FrameLayout) findViewById(R.id.card_top);
    final FrameLayout bottomCard = (FrameLayout) findViewById(R.id.card_bottom);

    final FrameLayout rightCard = (FrameLayout) findViewById(R.id.card_front_right);
    rightCard.setX(width);

    final FrameLayout leftCard = (FrameLayout) findViewById(R.id.card_front_left);
    leftCard.setX(-width);



    mainFrame.setOnTouchListener(new OnSwipeTouchListener(flashCards.this) {

      public void onSwipeTop() {
        //Toast.makeText(flashCards.this, "top", Toast.LENGTH_SHORT).show();
        bottomSwiped = true;
        flipCard(mainFrame);
      }

      public void onSwipeRight() {
        //Toast.makeText(flashCards.this, "right", Toast.LENGTH_SHORT).show();
        frontCard.animate()
                .translationXBy(width)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                  @Override
                  public void onAnimationStart(Animator animator) {

                  }

                  @Override
                  public void onAnimationEnd(Animator animator) {
                    frontCard.setTranslationX(0.0f);
                  }

                  @Override
                  public void onAnimationCancel(Animator animator) {

                  }

                  @Override
                  public void onAnimationRepeat(Animator animator) {

                  }
                });
        leftCard.animate()
                .translationXBy(width)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                  @Override
                  public void onAnimationStart(Animator animator) {
                    if (mIsBackVisible){
                      onSwipeTap();
                    } else if (mIsTopVisible) {
                      if (initatedFromBack){
                        onSwipeTop();
                        onSwipeTap();
                      } else {
                        onSwipeTop();
                      }
                    } else if (mIsBottomVisible) {
                      if (initatedFromBack) {
                        onSwipeBottom();
                        onSwipeTap();
                      } else {
                        onSwipeBottom();
                      }
                    }
                  }

                  @Override
                  public void onAnimationEnd(Animator animator) {
                    leftCard.setTranslationX(-width);

                    if (counter - 1 < 0) {
                      counter = flashcardSize - 1;
                    } else {
                      counter = counter - 1;
                    }

                    frontText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getCurrentPosition()));
                    backText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedBack,getCurrentPosition()));
                    topText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedTop,getCurrentPosition()));
                    bottomText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedBottom,getCurrentPosition()));
                    leftText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getLeftPosition()));
                    rightText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getRightPosition()));

                  }

                  @Override
                  public void onAnimationCancel(Animator animator) {

                  }

                  @Override
                  public void onAnimationRepeat(Animator animator) {

                  }
                });
      }
      public void onSwipeLeft() {
        //Toast.makeText(flashCards.this, "left", Toast.LENGTH_SHORT).show();
        frontCard.animate()
                .translationXBy(-width)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                  @Override
                  public void onAnimationStart(Animator animator) {
                    if (mIsBackVisible){
                      onSwipeTap();
                    } else if (mIsTopVisible) {
                      if (initatedFromBack){
                        onSwipeTop();
                        onSwipeTap();
                      } else {
                        onSwipeTop();
                      }
                    } else if (mIsBottomVisible) {
                      if (initatedFromBack) {
                        onSwipeBottom();
                        onSwipeTap();
                      } else {
                        onSwipeBottom();
                      }
                    }

                  }

                  @Override
                  public void onAnimationEnd(Animator animator) {
                    frontCard.setTranslationX(0.0f);
                  }

                  @Override
                  public void onAnimationCancel(Animator animator) {

                  }

                  @Override
                  public void onAnimationRepeat(Animator animator) {

                  }
                });
        rightCard.animate()
                .translationXBy(-width)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                  @Override
                  public void onAnimationStart(Animator animator) {

                  }

                  @Override
                  public void onAnimationEnd(Animator animator) {
                    rightCard.setTranslationX(width);
                    if (counter + 1 > flashcardSize -1) {
                      counter = 0;
                    } else {
                      counter = counter + 1;
                    }

                    frontText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getCurrentPosition()));
                    backText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedBack,getCurrentPosition()));
                    topText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedTop,getCurrentPosition()));
                    bottomText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedBottom,getCurrentPosition()));
                    leftText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getLeftPosition()));
                    rightText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getRightPosition()));

                  }

                  @Override
                  public void onAnimationCancel(Animator animator) {

                  }

                  @Override
                  public void onAnimationRepeat(Animator animator) {

                  }
                });
      }
      public void onSwipeBottom() {
        //Toast.makeText(flashCards.this, "bottom", Toast.LENGTH_SHORT).show();
        topSwiped = true;
        flipCard(mainFrame);
      }

      public void onSwipeTap() {
        //Toast.makeText(FlashCards2.this, "tapped", Toast.LENGTH_SHORT).show();
        tapInitiated = true;
        flipCard(mainFrame);

      }

    });


  }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

  private void changeCameraDistance() {
    int distance = 8000;
    float scale = getResources().getDisplayMetrics().density * distance;
    mCardFrontLayout.setCameraDistance(scale);
    mCardBackLayout.setCameraDistance(scale);
    mCardTopLayout.setCameraDistance(scale);
    mCardBottomLayout.setCameraDistance(scale);
  }

  private void loadAnimations() {
    mSetOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
    mSetIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);
    mSetTopIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.topin_animation);
    mSetTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.topout_animation);
    mSetBottomIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottomin_animation);
    mSetBottomOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottomout_animation);
    mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.leftin_animation);
    mSetLeftOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.leftout_animation);
  }

  private void findViews() {
    mCardBackLayout = findViewById(R.id.card_back);
    mCardFrontLayout = findViewById(R.id.card_front);
    mCardTopLayout = findViewById(R.id.card_top);
    mCardBottomLayout = findViewById(R.id.card_bottom);
  }

  public int getLeftPosition() {
    int leftPosition;
    if (counter - 1 < 0) {
      leftPosition = positionNumbers[flashcardSize-1];
    } else {
      leftPosition = positionNumbers[counter-1];
    }
    return leftPosition;
  }

  public int getRightPosition() {
    int rightPosition;
    if (counter + 1 > flashcardSize - 1) {
      rightPosition = positionNumbers[0];
    } else {
      rightPosition = positionNumbers[counter+1];
    }
    return rightPosition;
  }

  public int getCurrentPosition() {
    int thisPosition;
    thisPosition = positionNumbers[counter];
    return thisPosition;
  }



  public void flipCard(View view) {

    if (mIsFrontVisible) {
      if (tapInitiated && !topSwiped && !bottomSwiped && !mIsBackVisible && mIsFrontVisible && !mIsTopVisible && !initatedFromBack && !mIsBottomVisible){
        //front to back
        mSetOut.setTarget(mCardFrontLayout);
        mSetIn.setTarget(mCardBackLayout);
        mSetOut.start();
        mSetIn.start();

        mIsBackVisible = true;
        mIsFrontVisible = false;


      } else if (!tapInitiated && topSwiped && !bottomSwiped && !mIsBackVisible && mIsFrontVisible && !mIsTopVisible && !initatedFromBack && !mIsBottomVisible){
        //front to top
        mSetTopOut.setTarget(mCardFrontLayout);
        mSetTopIn.setTarget(mCardTopLayout);
        mSetTopOut.start();
        mSetTopIn.start();

        mIsTopVisible = true;
        mIsFrontVisible = false;

      } else if (!tapInitiated && !topSwiped && bottomSwiped && !mIsBackVisible && mIsFrontVisible && !mIsTopVisible && !initatedFromBack && !mIsBottomVisible) {
        //front to bottom
        mSetBottomOut.setTarget(mCardFrontLayout);
        mSetBottomIn.setTarget(mCardBottomLayout);
        mSetBottomOut.start();
        mSetBottomIn.start();

        mIsBottomVisible = true;
        mIsFrontVisible = false;
      }
    } else if (mIsBackVisible) {
      if (tapInitiated && !topSwiped && !bottomSwiped && mIsBackVisible && !mIsFrontVisible && !mIsTopVisible && !initatedFromBack && !mIsBottomVisible){
        //back to front
        mSetLeftOut.setTarget(mCardBackLayout);
        mSetLeftIn.setTarget(mCardFrontLayout);
        mSetLeftOut.start();
        mSetLeftIn.start();

        mIsBackVisible = false;
        mIsFrontVisible = true;

      } else if (!tapInitiated && topSwiped && !bottomSwiped && mIsBackVisible && !mIsFrontVisible && !mIsTopVisible && !initatedFromBack && !mIsBottomVisible){
        //back to top
        mSetTopOut.setTarget(mCardBackLayout);
        mSetTopIn.setTarget(mCardTopLayout);
        mSetTopOut.start();
        mSetTopIn.start();

        mIsTopVisible = true;
        mIsBackVisible = false;
        initatedFromBack = true;
      } else if (!tapInitiated && !topSwiped && bottomSwiped && mIsBackVisible && !mIsFrontVisible && !mIsTopVisible && !initatedFromBack && !mIsBottomVisible){
        //back to bottom
        mSetBottomOut.setTarget(mCardBackLayout);
        mSetBottomIn.setTarget(mCardBottomLayout);
        mSetBottomOut.start();
        mSetBottomIn.start();

        mIsBottomVisible = true;
        mIsBackVisible = false;
        initatedFromBack = true;
      }
    } else if (mIsTopVisible) {
      if (!tapInitiated && !topSwiped && bottomSwiped && !mIsBackVisible && !mIsFrontVisible && mIsTopVisible && !initatedFromBack && !mIsBottomVisible){
        //top to front
        mSetBottomOut.setTarget(mCardTopLayout);
        mSetBottomIn.setTarget(mCardFrontLayout);
        mSetBottomOut.start();
        mSetBottomIn.start();

        mIsTopVisible = false;
        mIsFrontVisible = true;

      } else if (!tapInitiated && !topSwiped && bottomSwiped && !mIsBackVisible && !mIsFrontVisible && mIsTopVisible && initatedFromBack && !mIsBottomVisible){
        //top to back
        mSetBottomOut.setTarget(mCardTopLayout);
        mSetBottomIn.setTarget(mCardBackLayout);
        mSetBottomOut.start();
        mSetBottomIn.start();

        mIsTopVisible = false;
        mIsBackVisible = true;
        initatedFromBack = false;
      }
    } else if (mIsBottomVisible) {
      if (!tapInitiated && topSwiped && !bottomSwiped && !mIsBackVisible && !mIsFrontVisible && !mIsTopVisible && !initatedFromBack && mIsBottomVisible) {
        //bottom to front
        mSetTopOut.setTarget(mCardBottomLayout);
        mSetTopIn.setTarget(mCardFrontLayout);
        mSetTopOut.start();
        mSetTopIn.start();

        mIsBottomVisible = false;
        mIsFrontVisible = true;
      } else if (!tapInitiated && topSwiped && !bottomSwiped && !mIsBackVisible && !mIsFrontVisible && !mIsTopVisible && initatedFromBack && mIsBottomVisible){
        //bottom to back
        mSetTopOut.setTarget(mCardBottomLayout);
        mSetTopIn.setTarget(mCardBackLayout);
        mSetTopOut.start();
        mSetTopIn.start();

        mIsBottomVisible = false;
        mIsBackVisible = true;
        initatedFromBack = false;
      }
    }
    tapInitiated = false;
    topSwiped = false;
    bottomSwiped = false;
  }

}