# Files

Files:

- `vmdv`: folder of the eclipse project `vmdv`, written in programming language Java;
- `sctl_visualization`: folder of a proof system SCTLProV, written in programming language OCaml;
- `visualization.mp4`: a short video of `vmdv`.


# Usage:
1. Install OCaml;
2. Open the folder `sctl_visualization`, and compile the source code (`make win` in windows, or `make linux` in linux);
3. Open the folder `vmdv`, and import the project to eclipse;
4. Find `VisualizAgent.java` in the package `vmdv.network`, and then `Run As --> Java Application` in the Package Explorer.
5. Go to the folder of SCTLProV (i.e., `sctl_visualization`), and run SCTLProV (e.g., `sctl -output output.out river.model`).