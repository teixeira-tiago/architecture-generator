package files.dataflow;

import architectures.dataflows.javagraphs.DataflowGraph;
import architectures.dataflows.graph.Node;
import architectures.dataflows.graph.RGraph;

public class graph50_2x2 extends DataflowGraph {

    RGraph graph = new RGraph();

    @Override
    public RGraph getGraph() {
        return graph;
    }

    public graph50_2x2() {
        try {
            Node t0_0 = new Node("t0_0", "IMUL", "IO");
            graph.addVertex(t0_0);

            Node t0_1 = new Node("t0_1", "IADD", "ALU");
            graph.addVertex(t0_1);

            Node t0_2 = new Node("t0_2", "COPY", "ALU");
            graph.addVertex(t0_2);

            Node t0_3 = new Node("t0_3", "IADD", "ALU");
            graph.addVertex(t0_3);

            Node t0_4 = new Node("t0_4", "COPY", "ALU");
            graph.addVertex(t0_4);

            Node t0_5 = new Node("t0_5", "COPY", "ALU");
            graph.addVertex(t0_5);

            Node t0_6 = new Node("t0_6", "IMUL", "MULT");
            graph.addVertex(t0_6);

            Node t0_7 = new Node("t0_7", "IMUL", "MULT");
            graph.addVertex(t0_7);

            Node t0_8 = new Node("t0_8", "IADD", "ALU");
            graph.addVertex(t0_8);

            Node t0_9 = new Node("t0_9", "IADD", "ALU");
            graph.addVertex(t0_9);

            Node t0_10 = new Node("t0_10", "COPY", "ALU");
            graph.addVertex(t0_10);

            Node t0_11 = new Node("t0_11", "IADD", "ALU");
            graph.addVertex(t0_11);

            Node t0_12 = new Node("t0_12", "COPY", "ALU");
            graph.addVertex(t0_12);

            Node t0_13 = new Node("t0_13", "COPY", "ALU");
            graph.addVertex(t0_13);

            Node t0_14 = new Node("t0_14", "COPY", "ALU");
            graph.addVertex(t0_14);

            Node t0_15 = new Node("t0_15", "IMUL", "MULT");
            graph.addVertex(t0_15);

            Node t0_16 = new Node("t0_16", "IMUL", "MULT");
            graph.addVertex(t0_16);

            Node t0_17 = new Node("t0_17", "IMUL", "MULT");
            graph.addVertex(t0_17);

            Node t0_18 = new Node("t0_18", "IADD", "ALU");
            graph.addVertex(t0_18);

            Node t0_19 = new Node("t0_19", "COPY", "ALU");
            graph.addVertex(t0_19);

            Node t0_20 = new Node("t0_20", "IMUL", "MULT");
            graph.addVertex(t0_20);

            Node t0_21 = new Node("t0_21", "IMUL", "MULT");
            graph.addVertex(t0_21);

            Node t0_22 = new Node("t0_22", "COPY", "ALU");
            graph.addVertex(t0_22);

            Node t0_23 = new Node("t0_23", "IMUL", "MULT");
            graph.addVertex(t0_23);

            Node t0_24 = new Node("t0_24", "IMUL", "MULT");
            graph.addVertex(t0_24);

            Node t0_25 = new Node("t0_25", "IADD", "ALU");
            graph.addVertex(t0_25);

            Node t0_26 = new Node("t0_26", "IADD", "ALU");
            graph.addVertex(t0_26);

            Node t0_27 = new Node("t0_27", "IMUL", "MULT");
            graph.addVertex(t0_27);

            Node t0_28 = new Node("t0_28", "IADD", "ALU");
            graph.addVertex(t0_28);

            Node t0_29 = new Node("t0_29", "IADD", "ALU");
            graph.addVertex(t0_29);

            Node t0_30 = new Node("t0_30", "COPY", "ALU");
            graph.addVertex(t0_30);

            Node t0_31 = new Node("t0_31", "COPY", "ALU");
            graph.addVertex(t0_31);

            Node t0_32 = new Node("t0_32", "COPY", "ALU");
            graph.addVertex(t0_32);

            Node t0_33 = new Node("t0_33", "IMUL", "MULT");
            graph.addVertex(t0_33);

            Node t0_34 = new Node("t0_34", "IADD", "ALU");
            graph.addVertex(t0_34);

            Node t0_35 = new Node("t0_35", "COPY", "ALU");
            graph.addVertex(t0_35);

            Node t0_36 = new Node("t0_36", "IMUL", "MULT");
            graph.addVertex(t0_36);

            Node t0_37 = new Node("t0_37", "IADD", "ALU");
            graph.addVertex(t0_37);

            Node t0_38 = new Node("t0_38", "COPY", "ALU");
            graph.addVertex(t0_38);

            Node t0_39 = new Node("t0_39", "IMUL", "MULT");
            graph.addVertex(t0_39);

            Node t0_40 = new Node("t0_40", "COPY", "ALU");
            graph.addVertex(t0_40);

            Node t0_41 = new Node("t0_41", "IADD", "ALU");
            graph.addVertex(t0_41);

            Node t0_42 = new Node("t0_42", "IMUL", "MULT");
            graph.addVertex(t0_42);

            Node t0_43 = new Node("t0_43", "IADD", "ALU");
            graph.addVertex(t0_43);

            Node t0_44 = new Node("t0_44", "COPY", "ALU");
            graph.addVertex(t0_44);

            Node t0_45 = new Node("t0_45", "COPY", "ALU");
            graph.addVertex(t0_45);

            Node t0_46 = new Node("t0_46", "COPY", "ALU");
            graph.addVertex(t0_46);

            Node t0_47 = new Node("t0_47", "COPY", "ALU");
            graph.addVertex(t0_47);

            Node t0_48 = new Node("t0_48", "COPY", "ALU");
            graph.addVertex(t0_48);

            Node t0_49 = new Node("t0_49", "IMUL", "MULT");
            graph.addVertex(t0_49);

            Node t0_50 = new Node("t0_50", "COPY", "ALU");
            graph.addVertex(t0_50);

            Node t0_51 = new Node("t0_51", "IMUL", "MULT");
            graph.addVertex(t0_51);

            Node t0_52 = new Node("t0_52", "IADD", "ALU");
            graph.addVertex(t0_52);

            Node t0_53 = new Node("t0_53", "IMUL", "MULT");
            graph.addVertex(t0_53);

            Node t0_54 = new Node("t0_54", "COPY", "ALU");
            graph.addVertex(t0_54);

            Node t0_55 = new Node("t0_55", "COPY", "ALU");
            graph.addVertex(t0_55);

            Node t0_56 = new Node("t0_56", "IADD", "ALU");
            graph.addVertex(t0_56);

            Node t0_57 = new Node("t0_57", "IADD", "ALU");
            graph.addVertex(t0_57);

            Node t0_58 = new Node("t0_58", "IADD", "ALU");
            graph.addVertex(t0_58);

            Node t0_59 = new Node("t0_59", "IMUL", "MULT");
            graph.addVertex(t0_59);

            Node t0_60 = new Node("t0_60", "IADD", "ALU");
            graph.addVertex(t0_60);

            Node t0_61 = new Node("t0_61", "IMUL", "MULT");
            graph.addVertex(t0_61);

            Node t0_62 = new Node("t0_62", "IMUL", "MULT");
            graph.addVertex(t0_62);

            Node t0_63 = new Node("t0_63", "IMUL", "IO");
            graph.addVertex(t0_63);

            graph.addEdge(t0_0, t0_1);
            graph.addEdge(t0_1, t0_2);
            graph.addEdge(t0_0, t0_3);
            graph.addEdge(t0_2, t0_4);
            graph.addEdge(t0_3, t0_5);
            graph.addEdge(t0_3, t0_6);
            graph.addEdge(t0_1, t0_7);
            graph.addEdge(t0_7, t0_8);
            graph.addEdge(t0_7, t0_9);
            graph.addEdge(t0_2, t0_10);
            graph.addEdge(t0_6, t0_10);
            graph.addEdge(t0_6, t0_11);
            graph.addEdge(t0_4, t0_11);
            graph.addEdge(t0_5, t0_12);
            graph.addEdge(t0_10, t0_13);
            graph.addEdge(t0_10, t0_14);
            graph.addEdge(t0_11, t0_15);
            graph.addEdge(t0_5, t0_15);
            graph.addEdge(t0_15, t0_16);
            graph.addEdge(t0_9, t0_17);
            graph.addEdge(t0_14, t0_18);
            graph.addEdge(t0_14, t0_19);
            graph.addEdge(t0_19, t0_20);
            graph.addEdge(t0_9, t0_20);
            graph.addEdge(t0_17, t0_21);
            graph.addEdge(t0_8, t0_22);
            graph.addEdge(t0_8, t0_23);
            graph.addEdge(t0_19, t0_24);
            graph.addEdge(t0_24, t0_25);
            graph.addEdge(t0_15, t0_25);
            graph.addEdge(t0_13, t0_26);
            graph.addEdge(t0_11, t0_27);
            graph.addEdge(t0_17, t0_27);
            graph.addEdge(t0_12, t0_28);
            graph.addEdge(t0_24, t0_29);
            graph.addEdge(t0_16, t0_29);
            graph.addEdge(t0_21, t0_30);
            graph.addEdge(t0_22, t0_31);
            graph.addEdge(t0_16, t0_32);
            graph.addEdge(t0_21, t0_33);
            graph.addEdge(t0_20, t0_34);
            graph.addEdge(t0_27, t0_35);
            graph.addEdge(t0_13, t0_36);
            graph.addEdge(t0_32, t0_37);
            graph.addEdge(t0_36, t0_38);
            graph.addEdge(t0_36, t0_39);
            graph.addEdge(t0_26, t0_40);
            graph.addEdge(t0_33, t0_41);
            graph.addEdge(t0_4, t0_41);
            graph.addEdge(t0_41, t0_42);
            graph.addEdge(t0_35, t0_43);
            graph.addEdge(t0_20, t0_43);
            graph.addEdge(t0_34, t0_44);
            graph.addEdge(t0_34, t0_45);
            graph.addEdge(t0_39, t0_46);
            graph.addEdge(t0_18, t0_46);
            graph.addEdge(t0_26, t0_47);
            graph.addEdge(t0_23, t0_47);
            graph.addEdge(t0_27, t0_48);
            graph.addEdge(t0_12, t0_48);
            graph.addEdge(t0_28, t0_49);
            graph.addEdge(t0_31, t0_49);
            graph.addEdge(t0_30, t0_50);
            graph.addEdge(t0_38, t0_50);
            graph.addEdge(t0_40, t0_51);
            graph.addEdge(t0_47, t0_51);
            graph.addEdge(t0_48, t0_52);
            graph.addEdge(t0_25, t0_52);
            graph.addEdge(t0_29, t0_53);
            graph.addEdge(t0_43, t0_53);
            graph.addEdge(t0_46, t0_54);
            graph.addEdge(t0_37, t0_54);
            graph.addEdge(t0_42, t0_55);
            graph.addEdge(t0_44, t0_55);
            graph.addEdge(t0_45, t0_56);
            graph.addEdge(t0_49, t0_56);
            graph.addEdge(t0_50, t0_57);
            graph.addEdge(t0_51, t0_57);
            graph.addEdge(t0_52, t0_58);
            graph.addEdge(t0_53, t0_58);
            graph.addEdge(t0_54, t0_59);
            graph.addEdge(t0_55, t0_59);
            graph.addEdge(t0_56, t0_60);
            graph.addEdge(t0_57, t0_60);
            graph.addEdge(t0_58, t0_61);
            graph.addEdge(t0_59, t0_61);
            graph.addEdge(t0_60, t0_62);
            graph.addEdge(t0_61, t0_62);
            graph.addEdge(t0_62, t0_63);
        } catch (Exception e) {
        }
    }
}
