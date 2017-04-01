# Files

Files in the folder:

- `vmdv.zip`: compression file of the eclipse project `vmdv`, written in programming language Java;
- `sctl_visualization.zip`: compression file of a proof system SCTLProV, written in programming language OCaml;
- `visualization.mp4`: a short video of `vmdv`.


# Usage:
1. Install OCaml;
2. Unzip the file `sctl_visualization.zip`, and compile the source code (`make win` in windows, or `make linux` in linux);
3. Unzip the file `vmdv.zip`, and import the project to eclipse;
4. Find `VisualizAgent.java` in the package `vmdv.network`, and then `Run As --> Java Application` in the Package Explorer.
5. Go to the folder of SCTLProV (e.g., `sctl_visualization`), and run SCTLProV (e.g., `sctl -output output.out river.model`).