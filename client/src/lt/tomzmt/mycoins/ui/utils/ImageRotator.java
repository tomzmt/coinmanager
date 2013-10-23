package lt.tomzmt.mycoins.ui.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

public class ImageRotator {

	private static final int ANIMATION_DURATION = 300;
	
	private static final float FIRST_ROTATE_START = 0;
	private static final float FIRST_ROTATE_END = 90;
	private static final float SECOND_ROTATE_START = 270;
	private static final float SECOND_ROTATE_END = 360;
	private static final float MAX_SCALE = 1.0f;
	private static final float MIN_SCALE = 0.5f;
	
    private final Interpolator accelerator = new AccelerateInterpolator();
    private final Interpolator decelerator = new DecelerateInterpolator();
    
    /**
     * 
     * @param view
     * @param command
     */
    @TargetApi(11)
	public void applyRotation(final View view, final RotationCommand command) {
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			
			PropertyValuesHolder zoomOutX = PropertyValuesHolder.ofFloat("scaleX", MAX_SCALE, MIN_SCALE);
			PropertyValuesHolder zoomOutY = PropertyValuesHolder.ofFloat("scaleY", MAX_SCALE, MIN_SCALE);
			PropertyValuesHolder rotate1 = PropertyValuesHolder.ofFloat("rotationY", FIRST_ROTATE_START, FIRST_ROTATE_END);
			ObjectAnimator visToInvis = ObjectAnimator.ofPropertyValuesHolder(view, rotate1, zoomOutX, zoomOutY);
			visToInvis.setDuration(ANIMATION_DURATION);
			visToInvis.setInterpolator(accelerator);

			PropertyValuesHolder zoomInX = PropertyValuesHolder.ofFloat("scaleX", MIN_SCALE, MAX_SCALE);
			PropertyValuesHolder zoomInY = PropertyValuesHolder.ofFloat("scaleY", MIN_SCALE, MAX_SCALE);
			PropertyValuesHolder rotate2 = PropertyValuesHolder.ofFloat("rotationY", SECOND_ROTATE_START, SECOND_ROTATE_END);
			final ObjectAnimator invisToVis = ObjectAnimator.ofPropertyValuesHolder(view, rotate2, zoomInX, zoomInY);
			invisToVis.setDuration(ANIMATION_DURATION);
			invisToVis.setInterpolator(decelerator);
	    
			visToInvis.addListener(new AnimatorListenerAdapter() {
				@Override public void onAnimationEnd(Animator anim) {
					if (command != null) {
						command.execute();
					}
					invisToVis.start();
				}
			});
			visToInvis.start();
			
		} else {
			final float centerX = view.getWidth() / 2.0f;
			final float centerY = view.getHeight() / 2.0f;

			final AnimationSet visToInvis = new AnimationSet(true);
			visToInvis.setDuration(ANIMATION_DURATION);
			visToInvis.setInterpolator(accelerator);
			visToInvis.setFillAfter(false);
			
			Rotate3dAnimation rotate1 = new Rotate3dAnimation(FIRST_ROTATE_START, FIRST_ROTATE_END, centerX, centerY, 0.0f, true);
			rotate1.setDuration(ANIMATION_DURATION);
			rotate1.setInterpolator(accelerator);
			rotate1.setFillAfter(false);
			
			visToInvis.addAnimation(rotate1);
			
			ScaleAnimation scale1 = new ScaleAnimation(MAX_SCALE, MIN_SCALE, MAX_SCALE, MIN_SCALE, centerX, centerY);
			scale1.setDuration(ANIMATION_DURATION);
			scale1.setInterpolator(accelerator);
			scale1.setFillAfter(false);
			
			visToInvis.addAnimation(scale1);
			
			final AnimationSet invisToVis = new AnimationSet(true);
			invisToVis.setDuration(ANIMATION_DURATION);
			invisToVis.setInterpolator(decelerator);
			invisToVis.setFillAfter(false);
			
			Rotate3dAnimation rotate2 = new Rotate3dAnimation(SECOND_ROTATE_START, SECOND_ROTATE_END, centerX, centerY, 0.0f, false);
			rotate2.setDuration(ANIMATION_DURATION);
			rotate2.setInterpolator(decelerator);
			rotate2.setFillAfter(false);
			
			invisToVis.addAnimation(rotate2);
			
			ScaleAnimation scale2 = new ScaleAnimation(MIN_SCALE, MAX_SCALE, MIN_SCALE, MAX_SCALE, centerX, centerY);
			scale2.setDuration(ANIMATION_DURATION);
			scale2.setInterpolator(decelerator);
			scale2.setFillAfter(false);
			
			invisToVis.addAnimation(scale2);
			
			rotate1.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					if (command != null) {
						command.execute();
					}
					view.startAnimation(invisToVis);
				}

				public void onAnimationRepeat(Animation animation) {
				}
			});
			
			view.startAnimation(visToInvis);
		}
	}
	
	/**
	 * 
	 *
	 */
	public interface RotationCommand {
		void execute();
	}
}
