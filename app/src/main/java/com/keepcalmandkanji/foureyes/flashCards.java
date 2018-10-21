package com.keepcalmandkanji.foureyes;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
  int counter;
  int[] positionNumbers;
  List arrayBucket1;
  List arrayBucket2;
  List arrayBucket3;
  List arrayBucket4;
  List arrayBucket5;
  int currentBucket;
  int width;
  int height;
  int mWidth;
  int mHeight;
  FrameLayout frontCard;
  FrameLayout backCard;
  FrameLayout topCard;
  FrameLayout bottomCard;
  FrameLayout leftCard;
  FrameLayout rightCard;
  TextView frontText;
  TextView backText;
  TextView leftText;
  TextView rightText;
  TextView topText;
  TextView bottomText;
  String selectedTable;
  String selectedFront;
  String selectedTop;
  String selectedBottom;
  String selectedBack;




  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_flash_cards);

    mWidth= this.getResources().getDisplayMetrics().widthPixels/2;
    mHeight= this.getResources().getDisplayMetrics().heightPixels/2;

    //set up database
    databaseAccess = DatabaseAccess.getInstance(this);
    databaseAccess.open();

    //Get TextViews
    frontText = findViewById(R.id.frontText);
    backText = findViewById(R.id.backText);
    topText = findViewById(R.id.topText);
    bottomText = findViewById(R.id.bottomText);
    leftText = findViewById(R.id.leftText);
    rightText = findViewById(R.id.rightText);

    //get level text
    TextView lvl1 = findViewById(R.id.lvl1);
    TextView lvl2 = findViewById(R.id.lvl2);
    TextView lvl3 = findViewById(R.id.lvl3);
    TextView lvl4 = findViewById(R.id.lvl4);
    TextView lvl5 = findViewById(R.id.lvl5);

    //get data from main activity
    selectedTable = getIntent().getStringExtra("selectedTable");
    selectedFront = getIntent().getStringExtra("selectedFront");
    selectedBack = getIntent().getStringExtra("selectedBack");
    selectedTop = getIntent().getStringExtra("selectedTop");
    selectedBottom = getIntent().getStringExtra("selectedBottom");

    positionNumbers = getIntent().getIntArrayExtra("positionNumbers");
    for (int i = 0; i < positionNumbers.length; i ++){
      //Log.i("BLAH","Received Positions: " + Integer.toString(positionNumbers[i]));
    }

    //initialize buckets
    arrayBucket1 = new ArrayList();
    arrayBucket2 = new ArrayList();
    arrayBucket3 = new ArrayList();
    arrayBucket4 = new ArrayList();
    arrayBucket5 = new ArrayList();

    for (int i = 0; i < positionNumbers.length; i++){
      arrayBucket1.add(positionNumbers[i]);
    }

    //for (int i = 0; i < arrayBucket1.size(); i++){
    //  Log.i("BLAH",arrayBucket1.get(i).toString());
    //}


    counter = 0;
    currentBucket = 1;

    if (currentBucket == 1){
      positionNumbers = new int[positionNumbers.length];
      for (int i = 0; i < positionNumbers.length; i++)
      positionNumbers[i] = Integer.parseInt(arrayBucket1.get(i).toString());
    }

    lvl1.setText(Integer.toString(positionNumbers.length));

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
    height = displayMetrics.heightPixels;
    width = displayMetrics.widthPixels;

    final FrameLayout mainFrame = findViewById(R.id.mainFrame);
    backCard = findViewById(R.id.card_back);
    frontCard = findViewById(R.id.card_front);
    topCard = findViewById(R.id.card_top);
    bottomCard = findViewById(R.id.card_bottom);

    rightCard = (FrameLayout) findViewById(R.id.card_front_right);
    rightCard.setX(width);

    leftCard = (FrameLayout) findViewById(R.id.card_front_left);
    leftCard.setX(-width);

    MovableFloatingActionButton correctButton = (MovableFloatingActionButton) findViewById(R.id.correct);
    MovableFloatingActionButton wrongButton = (MovableFloatingActionButton) findViewById(R.id.wrong);

    wrongButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //Toast.makeText(flashCards.this, "WRONG!! "+arrayBucket1.get(counter), Toast.LENGTH_SHORT).show();
        simulateSwipeLeft(view);
      }
    });

    correctButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //Toast.makeText(flashCards.this, "CORRECT! "+arrayBucket1.get(counter), Toast.LENGTH_SHORT).show();
        if (currentBucket < 5) {
            getNextBucketArray().add(getThisBucketArray().get(counter));
            getThisBucketArray().remove(getThisBucketArray().get(counter));
        } else if (currentBucket == 5 && positionNumbers.length == 1) {
            Toast.makeText(flashCards.this, "CONGRATULATIONS!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            getThisBucketArray().remove(getThisBucketArray().get(counter));
        }
        if (positionNumbers.length == 1) {
            if (currentBucket == 1) {
                currentBucket = 2;
                counter = 0;
            } else if (currentBucket == 2) {
                currentBucket = 3;
                counter = 0;
            } else if (currentBucket == 3) {
                currentBucket = 4;
                counter = 0;
            } else if (currentBucket == 4) {
                currentBucket = 5;
                counter = 0;
            }
        }
          counter = counter -1;
          updatePositionNumbers();
          lvl1.setText(Integer.toString(arrayBucket1.size()));
          lvl2.setText(Integer.toString(arrayBucket2.size()));
          lvl3.setText(Integer.toString(arrayBucket3.size()));
          lvl4.setText(Integer.toString(arrayBucket4.size()));
          lvl5.setText(Integer.toString(arrayBucket5.size()));

          rightText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,getRightPosition()));
          //rightText.setText(databaseAccess.getItemAtPosition(selectedTable,selectedFront,Integer.parseInt(getThisBucketArray().get(counter+1).toString())));
          //rightText.setText();
          simulateSwipeLeft(view);
      }

    });


    mainFrame.setOnTouchListener(new OnSwipeTouchListener(flashCards.this) {

      @SuppressLint("ClickableViewAccessibility")

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
                      counter = positionNumbers.length - 1;
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
                    if (counter + 1 > positionNumbers.length -1) {
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

  /*
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
  */

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
      leftPosition = positionNumbers[positionNumbers.length-1];
    } else {
      leftPosition = positionNumbers[counter-1];
    }
    return leftPosition;
  }

  public int getRightPosition() {
    int rightPosition;
    if (counter + 1 > positionNumbers.length - 1) {
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

  public void updatePositionNumbers() {
      if (currentBucket == 1){
          positionNumbers = new int[arrayBucket1.size()];
          for (int i = 0; i < positionNumbers.length; i++)
              positionNumbers[i] = Integer.parseInt(arrayBucket1.get(i).toString());
      } else if (currentBucket == 2) {
          positionNumbers = new int[arrayBucket2.size()];
          for (int i = 0; i < positionNumbers.length; i++)
              positionNumbers[i] = Integer.parseInt(arrayBucket2.get(i).toString());
      } else if (currentBucket == 3) {
          positionNumbers = new int[arrayBucket3.size()];
          for (int i = 0; i < positionNumbers.length; i++)
              positionNumbers[i] = Integer.parseInt(arrayBucket3.get(i).toString());
      } else if (currentBucket == 4) {
          positionNumbers = new int[arrayBucket4.size()];
          for (int i = 0; i < positionNumbers.length; i++)
              positionNumbers[i] = Integer.parseInt(arrayBucket4.get(i).toString());
      } else if (currentBucket == 5) {
          positionNumbers = new int[arrayBucket5.size()];
          for (int i = 0; i < positionNumbers.length; i++)
              positionNumbers[i] = Integer.parseInt(arrayBucket5.get(i).toString());
      } else {
          Toast.makeText(this, "Error: updatePositionNumbers", Toast.LENGTH_SHORT).show();
      }
  }

  public List getThisBucketArray() {
      if (currentBucket == 1) {
          return arrayBucket1;
      } else if (currentBucket == 2) {
          return arrayBucket2;
      } else if (currentBucket == 3) {
          return arrayBucket3;
      } else if (currentBucket == 4) {
          return arrayBucket4;
      } else {
          return arrayBucket5;
      }
  }

    public List getNextBucketArray() {
        if (currentBucket == 1) {
            return arrayBucket2;
        } else if (currentBucket == 2) {
            return arrayBucket3;
        } else if (currentBucket == 3) {
            return arrayBucket4;
        } else {
            return arrayBucket5;
        }
    }

    public List getLastBucketArray() {
        if (currentBucket == 1) {
            return arrayBucket5;
        } else if (currentBucket == 2) {
            return arrayBucket1;
        } else if (currentBucket == 3) {
            return arrayBucket2;
        } else if (currentBucket == 4) {
            return arrayBucket3;
        } else {
            return arrayBucket4;
        }
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

    public void simulateSwipeLeft(View view) {
        frontCard.animate()
                .translationXBy(-width)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                        if (mIsBackVisible){
                            tapInitiated = true;
                            flipCard(mainFrame);
                        } else if (mIsTopVisible) {
                            if (initatedFromBack){
                                bottomSwiped = true;
                                flipCard(mainFrame);
                                tapInitiated = true;
                                flipCard(mainFrame);
                            } else {
                                bottomSwiped = true;
                                flipCard(mainFrame);
                            }
                        } else if (mIsBottomVisible) {
                            if (initatedFromBack) {
                                topSwiped = true;
                                flipCard(mainFrame);
                                tapInitiated = true;
                                flipCard(mainFrame);
                            } else {
                                topSwiped = true;
                                flipCard(mainFrame);
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

                        if (counter + 1 > positionNumbers.length -1) {
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

}