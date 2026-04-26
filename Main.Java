import javax.swing.*;              // Swing
import java.awt.Color;             // Color
import java.awt.Dimension;         // Dimension
import java.awt.Graphics;          // Graphics
import java.awt.BorderLayout;      // BorderLayout
import java.util.ArrayList;        // ArrayList
import java.util.List;             // List
import java.util.Queue;            // Queue
import java.util.LinkedList;       // LinkedList
import java.util.Stack;            // Stack
import java.util.Scanner;          // Scanner
import java.util.HashSet;          // HashSet
import java.util.Set;              // Set

public class Main extends JPanel {

    static List<List<int[]>> graph;        // Adjacency list (neighbor, weight)
    static boolean[] visited;              // Visited array
    static int n;                          // Vertex count
    static boolean directed;               // Directed flag

    static List<Integer> order = new ArrayList<>(); // Traversal order
    static Set<String> treeEdges = new HashSet<>(); // Traversed edges

    static int currentNode = -1;           // Active node (animation)

    int[][] pos;                           // Node positions
    JTextArea output;                      // Output area

    // ===== CONSTRUCTOR =====
    public Main(int n, JTextArea output) {
        this.output = output;                          // Save output
        setPreferredSize(new Dimension(900, 600));     // Panel size
        setBackground(Color.WHITE);                    // Background

        pos = new int[n][2];                           // Position array

        int centerX = 450;                             // Circle center X
        int centerY = 300;                             // Circle center Y
        int radius = 220;                              // Radius

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;        // Equal spacing
            pos[i][0] = centerX + (int)(radius * Math.cos(angle));
            pos[i][1] = centerY + (int)(radius * Math.sin(angle));
        }
    }

    // ===== RESET =====
    static void reset(Main p) {
        visited = new boolean[n];      // Reset visited
        order.clear();                 // Clear order
        treeEdges.clear();             // Clear edges
        currentNode = -1;              // Reset active node
        p.repaint();                   // Redraw
    }

    // ===== ANIMATED BFS =====
    static void BFS(int start, Main p) {

        reset(p);

        Queue<Integer> q = new LinkedList<>();
        visited[start] = true;
        q.add(start);

        javax.swing.Timer timer = new javax.swing.Timer(800, null);

        timer.addActionListener(e -> {

            if (!q.isEmpty()) {

                int node = q.poll();
                currentNode = node;
                order.add(node);

                for (int[] arr : graph.get(node)) {
                    int neigh = arr[0];
                    if (!visited[neigh]) {
                        visited[neigh] = true;
                        q.add(neigh);
                        treeEdges.add(node + "-" + neigh);
                    }
                }

                p.output.setText("BFS: " + order);
                p.repaint();

            } else {
                currentNode = -1;
                timer.stop();
            }

        });

        timer.start();
    }

    // ===== ANIMATED DFS =====
    static void DFS(int start, Main p) {

        reset(p);

        Stack<Integer> stack = new Stack<>();
        stack.push(start);

        javax.swing.Timer timer = new javax.swing.Timer(800, null);

        timer.addActionListener(e -> {

            if (!stack.isEmpty()) {

                int node = stack.pop();

                if (!visited[node]) {
                    visited[node] = true;
                    currentNode = node;
                    order.add(node);

                    for (int[] arr : graph.get(node)) {
                        int neigh = arr[0];
                        if (!visited[neigh]) {
                            stack.push(neigh);
                            treeEdges.add(node + "-" + neigh);
                        }
                    }

                    p.output.setText("DFS: " + order);
                    p.repaint();
                }

            } else {
                currentNode = -1;
                timer.stop();
            }

        });

        timer.start();
    }

    // ===== CONNECTED COMPONENTS =====
    static int totalComponents() {
        boolean[] vis = new boolean[n];
        int count = 0;

        for (int i = 0; i < n; i++) {
            if (!vis[i]) {
                explore(i, vis);
                count++;
            }
        }
        return count;
    }

    static void explore(int node, boolean[] vis) {
        vis[node] = true;
        for (int[] arr : graph.get(node)) {
            int neigh = arr[0];
            if (!vis[neigh])
                explore(neigh, vis);
        }
    }

    // ===== DRAW GRAPH =====
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw edges
        for (int i = 0; i < n; i++) {
            for (int[] arr : graph.get(i)) {
                int neigh = arr[0];

                if (treeEdges.contains(i + "-" + neigh))
                    g.setColor(Color.RED);       // Traversed edge
                else
                    g.setColor(Color.BLACK);     // Normal edge

                g.drawLine(pos[i][0], pos[i][1],
                        pos[neigh][0], pos[neigh][1]);
            }
        }

        // Draw nodes
        for (int i = 0; i < n; i++) {

            if (i == currentNode)
                g.setColor(Color.YELLOW);        // Active
            else if (visited != null && visited[i])
                g.setColor(Color.GREEN);         // Visited
            else
                g.setColor(Color.LIGHT_GRAY);    // Default

            g.fillOval(pos[i][0] - 20, pos[i][1] - 20, 40, 40);
            g.setColor(Color.BLACK);
            g.drawString("" + i, pos[i][0] - 5, pos[i][1] + 5);
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.print("Vertices: ");
        n = in.nextInt();

        System.out.print("Edges: ");
        int e = in.nextInt();

        System.out.print("Directed (true/false): ");
        directed = in.nextBoolean();

        graph = new ArrayList<>();
        for (int i = 0; i < n; i++)
            graph.add(new ArrayList<>());

        for (int i = 0; i < e; i++) {
            System.out.print("Edge u v weight: ");
            int u = in.nextInt();
            int v = in.nextInt();
            int w = in.nextInt();

            graph.get(u).add(new int[]{v, w});
            if (!directed)
                graph.get(v).add(new int[]{u, w});
        }

        System.out.print("Start vertex: ");
        int start = in.nextInt();

        JFrame frame = new JFrame("Graph Visualizer");

        JTextArea out = new JTextArea(4, 50);
        out.setEditable(false);

        Main panel = new Main(n, out);

        JButton bfs = new JButton("BFS");
        JButton dfs = new JButton("DFS");
        JButton comp = new JButton("Components");

        bfs.addActionListener(x -> BFS(start, panel));
        dfs.addActionListener(x -> DFS(start, panel));
        comp.addActionListener(x ->
                out.setText("Components: " + totalComponents()));

        JPanel top = new JPanel();
        top.add(bfs);
        top.add(dfs);
        top.add(comp);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(top, BorderLayout.NORTH);
        frame.add(new JScrollPane(out), BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
