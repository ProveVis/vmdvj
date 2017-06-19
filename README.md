[TOC]

# VMDV

## Files

- `vmdv`: folder of the eclipse project `vmdv`, written in programming language Java;
- `sctl_visualization`: folder of a proof system SCTLProV, written in programming language OCaml;
- `visualization.mp4`: a short video of `vmdv`.

## Usage:

1. Install OCaml;
2. Open the folder `sctl_visualization`, and compile the source code (`make win` in windows, or `make linux` in linux);
3. Open the folder `vmdv`, and import the project to eclipse;
4. Find `VisualizAgent.java` in the package `vmdv.network`, and then `Run As --> Java Application` in the Package Explorer.
5. Go to the folder of SCTLProV (i.e., `sctl_visualization`), and run SCTLProV (e.g., `sctl -output output.out river.model`).



# VMDV2.0

## Files

* vmdv2.0: folder of the eclipse project `vmdv2.0`, written in programming language Java.

## Usage

1. Download the automated theorem prover [SCTLProV](https://github.com/terminatorlxj/SCTLProV), and then compile SCTLProV following the instructions of its readme file;
2. Open folder `vmdv2.0`, and then import this project into eclipse;
3. Find `VMDV.java` in the package `vmdv.control`, and then `Run As --> Java Application` in the Package Explorer;
4. Finally, run the tool SCTLProV with option `-visualize_addr <IP address>`(e.g., `sctl -visualize_addr 127.0.0.1 river.model`).

## Modification from version 1.0

1. Total refactor of the java classes and interfaces. The 2.0 version follows the MVC (i.e., Model-View-Controller) design pattern: 
   * Model: The inter-representation of two kinds of graphs: `Tree` and `DiGraph`, both inherit the abstract class `AbstractGraph`;
   * View: The main UI `Viewer`, and several kinds of event handlers including `GLEventHandler`, `KeyHandler`, `MouseHandler`, `MouseMotionHandler`, and `MouseWheelHandler`. 
   * Controller: The main class `VMDV`, and the layout algorithm of 3D graphs `GraphLayout`, which is an abstract class, so people can write their own layout algorithm, as long as they inherit `GraphLayout`.
2. Added a flexible communication protocol between theorem provers and `VMDV`. This protocol is specified in the file `protocol.md`. 
3.  Discard the folder `sctl_visualization`, and the visualization agent of the theorem prover `SCTLProV` is integerated into its [main repository](https://github.com/terminatorlxj/SCTLProV). 

# Author and Acknowledgment
This tool is written by Jian Liu (liujian@ios.ac.cn). The author would like to thank Ying Jiang (jy@ios.ac.cn) and Yanyun Chen (chenyy@ios.ac.cn) for their valuable remarks.
