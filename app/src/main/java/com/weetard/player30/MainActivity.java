package com.weetard.player30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements Runnable{

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SeekBar seekBar;
    private boolean wasPlaying = false;
    private FloatingActionButton fabPlayPause;
    private FloatingActionButton fabBack;
    private FloatingActionButton fabForward;

    private FloatingActionButton fabNext;
    private FloatingActionButton fabRepeat;
    private TextView seekBarHint;
    private TextView metaDataAudio;
    private String metaData;
    private boolean isRepeat = false;

    private int nextSong = 1;
    private String WhatSong = "Yeat-Move.mp3";
    private int wherePlayed = 0;

    private boolean needClear = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabPlayPause = findViewById(R.id.fabPlayPause);
        fabBack = findViewById(R.id.fabBack);
        fabRepeat = findViewById(R.id.fabRepeat);
        fabForward = findViewById(R.id.fabForward);
        seekBarHint = findViewById(R.id.seekBarHint);
        seekBar = findViewById(R.id.seekBar);
        fabNext = findViewById(R.id.fabNext);
        metaDataAudio = findViewById(R.id.metaDataAudio);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);

                wherePlayed = progress;

                int timeTrack = (int) Math.ceil(progress/1000f);

                if (timeTrack < 10) {
                    seekBarHint.setText("00:0" + timeTrack);
                } else if (timeTrack < 60){
                    seekBarHint.setText("00:" + timeTrack);
                } else if (timeTrack >= 60 && timeTrack < 70) {
                    seekBarHint.setText("01:0" + (timeTrack - 60));
                } else if (timeTrack >= 70 && timeTrack < 120) {
                    seekBarHint.setText("01:" + (timeTrack - 60));
                } else if (timeTrack >= 120 && timeTrack < 130) {
                    seekBarHint.setText("02:0" + (timeTrack - 120));
                } else if (timeTrack >= 130 && timeTrack < 180) {
                   seekBarHint.setText("02:" + (timeTrack - 120));
                } else if (timeTrack >= 180 && timeTrack < 190) {
                   seekBarHint.setText("03:0" + (timeTrack - 180));
                } else if (timeTrack >= 190) {
                   seekBarHint.setText("03:" + (timeTrack - 180));
                }


                double percentTrack = progress / (double) seekBar.getMax();

                seekBarHint.setX(seekBar.getX() + Math.round(seekBar.getWidth()*percentTrack*0.92));

                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    if (!isRepeat) {
                        clearMediaPlayer();
                        fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                        MainActivity.this.seekBar.setProgress(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });


        fabPlayPause.setOnClickListener(listener);
        fabBack.setOnClickListener(listener);
        fabRepeat.setOnClickListener(listener);
        fabForward.setOnClickListener(listener);
        fabNext.setOnClickListener(listener);
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.fabNext:
                    if (nextSong >= 5) {
                        nextSong = 1;
                    } else if (nextSong <= 5) {
                        nextSong++;
                    }
                    playNextSong();
                    break;
                case R.id.fabPlayPause:
                    playSong();
                    break;
                case R.id.fabBack:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                    break;
                case R.id.fabForward:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                    break;
                case R.id.fabRepeat:
                    if (!isRepeat && mediaPlayer != null) {
                        mediaPlayer.setLooping(true);
                        fabRepeat.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_menu_rotate));
                        isRepeat = true;
                    } else if (isRepeat && mediaPlayer != null){
                        mediaPlayer.setLooping(false);
                        fabRepeat.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.stat_notify_sync));
                        isRepeat = false;
                    }
                    break;
            }
        }
    };

    public void playNextSong() {
        switch(nextSong) {
            case 1:
                WhatSong = "Yeat-Move.mp3";
                break;
            case 2:
                WhatSong = "PlayboiCarti-Skeletons.mp3";
                break;
            case 3:
                WhatSong = "boomin.mp3";
                break;
            case 4:
                WhatSong = "KenCarSon-Yale.mp3";
                break;
            case 5:
                WhatSong = "Yeat-HowItgo.mp3";
                break;
        }
        needClear = true;
        playSong();
        playSong();
    }
    public void playSong() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    wherePlayed = mediaPlayer.getCurrentPosition();
                    clearMediaPlayer();
                    wasPlaying = true;
                    if (needClear) wherePlayed = 0;
                    needClear = false;
                    fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
            }


            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause));
                AssetFileDescriptor descriptor = getAssets().openFd(WhatSong);

                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());


                MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
                mediaMetadata.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());

                String author = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                if (author != null)
                    metaData = author;
                else metaData = "Иполнитель неизвестен";
                String Song = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                if (Song != null)
                    metaData += "\n" + Song;
                else metaData += "\n" + "Композициця неизвестна";
                mediaMetadata.release();

                metaDataAudio.setText(metaData);

                descriptor.close();

                mediaPlayer.prepare();
                mediaPlayer.setLooping(isRepeat);
                seekBar.setMax(mediaPlayer.getDuration());
                if (wherePlayed < mediaPlayer.getDuration()) {
                    mediaPlayer.seekTo(wherePlayed);
                }
                mediaPlayer.start();
                new Thread(this).start();

            }

            wasPlaying = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();
        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition);

        }
    }
}