package in.tagteen.tagteen.Adapters;


public class ChatArrayAdapter {
       /* extends ArrayAdapter<ChatMessage>implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private boolean isImage,isVedio;
    private String pathOfResource;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();

    private Context context;
    private String type[]={"TEXT","IMAGE","VIDEO","FILE","VOICE"};
    private boolean flag_play;
    private MediaPlayer mPlayer = null;
    ViewHolder holder;
    Handler seekHandler = new Handler();

    String typeOfFile;
    int rowItem;
    private Integer[] mThumbIds = {
            android.R.drawable.ic_media_play, android.R.drawable.ic_media_pause};

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context=context;
        mPlayer = new MediaPlayer();

    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMessage  chatMessageObj = getItem(position);
        View row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (chatMessageObj.left) {
                row = inflater.inflate(R.layout.right, parent, false);
            }else{
                row = inflater.inflate(R.layout.left, parent, false);
            }
            holder = new ViewHolder();
            holder.imageViewchat=(RoundedImageView) row.findViewById(R.id.imageView_chat);
            holder.imageLayout=(LinearLayout)row.findViewById(R.id.image_layout);
            holder.vedioLayout=(LinearLayout)row.findViewById(R.id.vedio_layout);
            holder.audioLayout=(LinearLayout)row.findViewById(R.id.voice_layout);
            holder.fileLayout=(LinearLayout) row.findViewById(R.id.file_layout);
            holder.textLayout=(RelativeLayout)row.findViewById(R.id.bubble_layout);
            holder.chatText = (EmojiconTextView) row.findViewById(R.id.msgr);
            holder.play_btn = (ImageView) row.findViewById(R.id.play_stop_button);
            holder.seekBar = (SeekBar) row.findViewById(R.id.seekbar_play);
            row.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
         isImage=chatMessageObj.isImage;
         holder.play_btn.setOnClickListener(new imageViewClickListene(position));


        if (isImage==true){

            typeOfFile=openFile(chatMessageObj.Path);
            if(typeOfFile.equals(type[0])){
                holder.imageLayout.setVisibility(View.GONE);
                holder.vedioLayout.setVisibility(View.GONE);
                holder.audioLayout.setVisibility(View.GONE);
                holder.fileLayout.setVisibility(View.GONE);
                holder.textLayout.setVisibility(View.VISIBLE);

            }else  if(typeOfFile.equals(type[1])){
                holder.imageLayout.setVisibility(View.VISIBLE);
                holder.vedioLayout.setVisibility(View.GONE);
                holder.audioLayout.setVisibility(View.GONE);
                holder.fileLayout.setVisibility(View.GONE);
                holder.textLayout.setVisibility(View.GONE);
                Glide
                        .with(context)
                        .load(chatMessageObj.Path)
                        .fitCenter()
                        .into(holder.imageViewchat);
                holder.chatText.setText(chatMessageObj.message);

            }else  if(typeOfFile.equals(type[2])){

                //"VIDEO"
                holder.imageLayout.setVisibility(View.GONE);
                holder.vedioLayout.setVisibility(View.VISIBLE);
                holder.audioLayout.setVisibility(View.GONE);
                holder.fileLayout.setVisibility(View.GONE);
                holder.textLayout.setVisibility(View.GONE);

            }else  if(typeOfFile.equals(type[3])){

              //  "FILE"
                holder.imageLayout.setVisibility(View.GONE);
                holder.vedioLayout.setVisibility(View.GONE);
                holder.audioLayout.setVisibility(View.GONE);
                holder.fileLayout.setVisibility(View.VISIBLE);
                holder.textLayout.setVisibility(View.GONE);
              }else{

              //  "VOICE"
                holder.imageLayout.setVisibility(View.GONE);
                holder.vedioLayout.setVisibility(View.GONE);
                holder.audioLayout.setVisibility(View.VISIBLE);
                holder.fileLayout.setVisibility(View.GONE);
                holder.textLayout.setVisibility(View.GONE);


                    }


            

        }else{

            holder.imageLayout.setVisibility(View.GONE);
            holder.vedioLayout.setVisibility(View.GONE);
            holder.audioLayout.setVisibility(View.GONE);
            holder.fileLayout.setVisibility(View.GONE);
            holder.textLayout.setVisibility(View.VISIBLE);
            holder.chatText.setText(chatMessageObj.message);
        }

     *//*   play_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!flag_play) {
                    play_btn.setImageResource(mThumbIds[1]);
                    startPlaying(chatMessageObj.Path);
                    flag_play = true;
                } else {
                    play_btn.setImageResource(mThumbIds[0]);
                    mPlayer.stop();
                    seekBar.setProgress(0);
                    flag_play = false;
                }
            }
        } );*//*

        return row;
    }


    private String openFile(String url) {
        String typeOfFile=null;
        try {
            
            if (url.contains(".doc") || url.contains(".docx")) {
            } else if (url.contains(".pdf")) {
                typeOfFile= type[3];
            } else if (url.contains(".ppt") || url.contains(".pptx")) {
                typeOfFile= type[3];
            } else if (url.contains(".xls") || url.contains(".xlsx")) {
                typeOfFile= type[3];
            } else if (url.contains(".zip") || url.contains(".rar")) {
                typeOfFile= type[3];
            } else if (url.contains(".rtf")) {
                typeOfFile= type[3];
            } else if (url.contains(".wav") || url.contains(".mp3")) {
                typeOfFile= type[4];
            } else if (url.contains(".gif")) {
                typeOfFile= type[1];
            } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
                typeOfFile= type[1];
            } else if (url.contains(".txt")) {
                typeOfFile= type[0];
            } else if (url.contains(".3gp") || url.contains(".mpg") ||
                    url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
                typeOfFile= type[2];
            } else {
                typeOfFile= type[3];
            }

        } catch (ActivityNotFoundException e) {
        }
        return typeOfFile;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    private void startPlaying(String path) {
        // TODO Auto-generated method stub

        try {

            mPlayer.reset();
            FileInputStream rawFile = new FileInputStream(path);
            Log.e("filename", path);
            mPlayer.setDataSource(rawFile.getFD());
            mPlayer.prepare();
            mPlayer.start();

            holder.seekBar.setProgress(0);
            holder.seekBar.setMax(100);

            updateProgressBar();

        } catch (IOException e) {
            Log.e("preparefailed", "prepare() failed");
        }

    }

    private void updateProgressBar() {
        // TODO Auto-generated method stub

        seekHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();
            String str1 = new String();

            int progress = (int) (getProgressPercentage(
                    currentDuration, totalDuration));
            holder.seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            seekHandler.postDelayed(this, 100);
            if (!mPlayer.isPlaying()) {
                holder.play_btn.setImageResource(mThumbIds[0]);

                flag_play = false;
            }
            if (!flag_play)
                holder.seekBar.setProgress(0);

        }
    };

        private int getProgressPercentage(long currentDuration,
                                          long totalDuration) {
            // TODO Auto-generated method stub
            Double percentage = (double) 0;

            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);

            // calculating percentage
            percentage = (((double) currentSeconds) / totalSeconds) * 100;

            // return percentage
            return percentage.intValue();
        }

    }
*/
}