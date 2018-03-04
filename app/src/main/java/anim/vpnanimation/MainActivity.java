package anim.vpnanimation;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

//done by Aleksei Jegorov 28.02.2018
public class MainActivity extends AppCompatActivity {

    TextView tvOutput;
    ImageView ivGif;
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = (TextView) findViewById(R.id.tv_output);
        ivGif = (ImageView) findViewById(R.id.iv_gif);
        ivGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state == 2) {
                    animationDisconnect();
                } else {
                    animationBegin();
                }
            }
        });

        //ivGif.setVisibility(View.VISIBLE);
    }


    protected void animationBegin() {
        state = 1;
        GlideApp.with(this)
                .load(R.drawable.connect_start)
                .placeholder(R.drawable.first_connect_start)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Handler handler = new Handler();
                        if (resource instanceof GifDrawable) {
                            try {
                                GifDrawable gifDrawable = (GifDrawable) resource;

                                Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                                Field frameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                                frameLoaderField.setAccessible(true);
                                Object frameLoader = frameLoaderField.get(gifDrawable.getConstantState());

                                Class frameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                                Field gifDecoderField = frameLoaderClass.getDeclaredField("gifDecoder");
                                gifDecoderField.setAccessible(true);
                                GifDecoder gifDecoder = (GifDecoder) gifDecoderField.get(frameLoader);

                                int duration = 0;
                                for (int i = 0; i < gifDrawable.getFrameCount(); i++) {
                                    duration += gifDecoder.getDelay(i);
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        animationProcessContinued();
                                    }
                                }, duration);
                                tvOutput.append("\nBegin: " + duration + " ms");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    animationProcessContinued();
                                }
                            }, 1320);
                        }
                        return false;
                    }
                })
                .useAnimationPool(true)
                .into(ivGif);
        /*
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ivGif.setImageDrawable(resource);
                    }
                });
*/
    }

    public static boolean isLowRamDevice(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager != null && ActivityManagerCompat.isLowRamDevice(activityManager);
    }

    protected void animationProcessContinued() {

        if(isLowRamDevice(getBaseContext())) {
            ivGif.setImageDrawable(getResources().getDrawable(R.drawable.first_connect_process));
        } else {
            GlideApp.with(this)
                    .load(R.drawable.connect_process)
                    .placeholder(R.drawable.first_connect_process)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Handler handler = new Handler();
                            int duration = 5000;

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    animationProcessFinished();
                                }
                            }, duration);

                            tvOutput.append("\nProcess: " + duration + " ms");
                            return false;
                        }
                    })
                    .useAnimationPool(true)
                    .into(ivGif);
        }
    }


    protected void animationProcessFinished() {
        GlideApp.with(this)
                .load(R.drawable.connect_done)
                .placeholder(R.drawable.first_connect_done)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Handler handler = new Handler();
                        if (resource instanceof GifDrawable) {
                            try {
                                GifDrawable gifDrawable = (GifDrawable) resource;

                                Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                                Field frameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                                frameLoaderField.setAccessible(true);
                                Object frameLoader = frameLoaderField.get(gifDrawable.getConstantState());

                                Class frameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                                Field gifDecoderField = frameLoaderClass.getDeclaredField("gifDecoder");
                                gifDecoderField.setAccessible(true);
                                GifDecoder gifDecoder = (GifDecoder) gifDecoderField.get(frameLoader);

                                int duration = 0;
                                for (int i = 0; i < gifDrawable.getFrameCount(); i++) {
                                    duration += gifDecoder.getDelay(i);
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        shieldTurnedOn();
                                    }
                                }, duration);
                                tvOutput.append("\nDone: " + duration + " ms");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    shieldTurnedOn();
                                }
                            }, TimeUnit.MILLISECONDS.toMillis(100));
                        }
                        return false;
                    }
                })
                .useAnimationPool(true)
                .into(ivGif);
    }

    protected void shieldTurnedOn() {
        ivGif.setImageDrawable(getResources().getDrawable(R.drawable.first_disconnect));
        state = 2;
    }

    protected void shieldTurnedOff() {
        ivGif.setImageDrawable(getResources().getDrawable(R.drawable.first_connect_start));
        state = 1;
    }

    protected void animationDisconnect() {
        GlideApp.with(this)
                .load(R.drawable.disconnect)
                .placeholder(R.drawable.first_disconnect)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Handler handler = new Handler();
                        if (resource instanceof GifDrawable) {
                            try {
                                GifDrawable gifDrawable = (GifDrawable) resource;

                                Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                                Field frameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                                frameLoaderField.setAccessible(true);
                                Object frameLoader = frameLoaderField.get(gifDrawable.getConstantState());

                                Class frameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                                Field gifDecoderField = frameLoaderClass.getDeclaredField("gifDecoder");
                                gifDecoderField.setAccessible(true);
                                GifDecoder gifDecoder = (GifDecoder) gifDecoderField.get(frameLoader);

                                int duration = 0;
                                for (int i = 0; i < gifDrawable.getFrameCount(); i++) {
                                    duration += gifDecoder.getDelay(i);
                                }
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        shieldTurnedOff();
                                    }
                                }, duration);
                                tvOutput.append("\nDisconnect: " + duration + " ms");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    shieldTurnedOff();
                                }
                            }, TimeUnit.MILLISECONDS.toMillis(870));
                        }
                        return false;
                    }
                })
                .useAnimationPool(true)
                .into(ivGif);
    }

}
