This is a simple search engine repo. 

#commends for nutch.

1. merge segments
nutch mergesegs <merged_seg_dir> -dir <segments>

2. parse merged segment
nutch parse <merged_seg>

3. dump segments
nutch readseg -dump <segment_dir> <output> -nocontent -nofetch -nogenerate -noparse
