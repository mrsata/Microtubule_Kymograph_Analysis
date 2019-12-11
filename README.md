# Microtubule Kymograph Analysis

ImageJ plugin for microtubule kymograph measurements and analysis.

## Prerequisites

This plugin requires [ImageJ](https://imagej.net/ImageJ2). 

We recommend installing the [Fiji](http://fiji.sc/) distribution of ImageJ. It includes the ImageJ software and many other plugins. The rest of this readme file will assume that the user is using the Fiji distribution.

## Installation

* Download the "Microtubule_Kymograph_Analysis-(version number).jar" fine on your own computer. The most recent version of the plugin can be found at the releases tab(coming soon) on the Microtubule_Kymograph_Analysis GitHub page.
* Save the file in the Fiji "plugins" folder.
* Restart Fiji, and the plugin should appear in “Process” -> “Microtubule Kymograph Analysis”

```
The numbers after the hyphen denote the version number of the plugin.
If you wish to install a more recent version, then you should delete the older file from the plugins folder
and replace it with the new one.
```

## Usage

* In Fiji, open a kymograph image that you would like to analyze. The plugin assumes the time axis of a kymograph image is vertical.

* Run the plugin (in “Process” -> “Microtubule Kymograph Analysis”).

* The UI has the following features:
  * Draw Left button - Reset statistics (if any) and prepares the user to draw a segmented line (polyline) to track activities on the left side of the kymograph image.
  * Draw Right button -  Reset statistics (if any) and prepares the user to draw a segmented line (polyline) to track activities on the right side of the kymograph image.
  * Clear/Show Overlay button - Remove/add an overlay along the segmented line (polyline) drawn on the image. This button has a hotkey: "Cntl". The role of the overlay will be explained in [the next section](#Overlay).
  * Output button - Save the current segmented line (polyline) and overlay in a .tiff file, and current statistics in a .csv file. The format of the csv file will be explained in [the next section](#Output).
  * Statistics display - Display the calculated statisctics of activities currently tracked.
  * Log window - Records events such as resetting, saving, clearing/showing the overlay, and inappropriate drawing, etc. 

* Use the segmented line (polyline) tool to trace microtubule activities on the kymographs.

* Start at the root, click at each point where the microtubule changes its phases (growth/shrink/pause), and stop when the microtubule shrinks back to the root. 

* Once you are content with the current tracking and statistics, you can press output to save them. You can reset and continue drawing on the same kymograph image after saving.
* To reset everything, click on the background to cancel the current segmented line (polyline) selection or press the "Draw left" or "Draw right" button (corresponding to the current side).

* You can close and open another image and analyze it without reopening the plugin. However, if you open multiple images, the plugin only works for the most recent one, and you have to reopen the plugin if you want to analyze another existing image.

* If you want to close the plugin, press the “x” button on the “Microtubule Kymograph Analysis” window.

## Design

### Overlay

- The overlay aligns a line of default width 2 to each segment of the segmented line (polyline). 
- Each line alignment has a color depending on the current phase of the activity indicated by the segment. The colors corresponding to the 4 kinds of phases are:
  - Green: growth
  - Red: shrink 
  - Blue: pause / no activity
  - No color: undefined (horizontal or upward line)

### Output

- The outputs are saved in the same directory as the opened image.
- The columns of the statistics sheet are: 
  - Index
  - Label
  - Phase
  - Distance (um)
  - Time (s)
  - Rategrowth (um/s)
  - Rateshrink (um/s)
  - Distancegrowth (um)
  - Distanceshrink (um)
  - Time growth (s)
  - Time shrink (s)
  - Catastrophe
  - Rescue
  - Catastrophe frequency
  - Rescue frequency

### Statistics

- The default rates for distance and time are 0.08 um/pixel and 2.5 second/pixel.

## Authors

* Han Liu - [mrsata](https://github.com/mrsata)
