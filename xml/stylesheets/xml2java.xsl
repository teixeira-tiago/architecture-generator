<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <!-- XML to Java XSLT Transformer 
        August, 2007
        University of Vicosa, Brazil
    -->

    <!-- TAB  char unicode = 9-->
    <xsl:output method="text" omit-xml-declaration="no" indent="yes"/>
    
    <!-- global variable indicating the value of the trace option -->
    <!--- <xsl:variable name="trace" select="/DESIGN/@trace"/> -->
    <xsl:variable name="topology" select="/DESIGN/@name"/>
    <xsl:variable name="nome" select="/DESIGN/COMPONENT/@name"></xsl:variable>
    <!-- end globals -->
    
    <xsl:template match="/">
        <xsl:text>package files.dataflow;&#xA;&#xA;</xsl:text>
        <xsl:text>import architectures.dataflows.graph.Node;&#xA;</xsl:text>
        <xsl:text>import architectures.dataflows.graph.RGraph;&#xA;</xsl:text>
        <xsl:text>import architectures.dataflows.javagraphs.DataflowGraph;&#xA;&#xA;</xsl:text>
        <xsl:text>public class </xsl:text>
        <xsl:value-of select="$topology"/>
        <xsl:text> extends DataflowGraph {&#xA;&#xA;</xsl:text>
        <xsl:text>&#9;RGraph graph = new RGraph();&#xA;&#xA;</xsl:text>
        <xsl:text>&#9;@Override&#xA;</xsl:text>        
        <xsl:text>&#9;public RGraph getGraph() {&#xA;</xsl:text>
        <xsl:text>&#9;&#9;return graph;&#xA;</xsl:text>
        <xsl:text>&#9;}&#xA;&#xA;</xsl:text>
        <!-- ### BEGIN generate method for creating design-->
        <xsl:text>&#9;public </xsl:text>
        <xsl:value-of select="$topology"/>
        <xsl:text>() {&#xA;</xsl:text>
        <xsl:text>&#9;&#9;try {&#xA;</xsl:text>
        <xsl:apply-templates select="//COMPONENT"/>
        <xsl:apply-templates select="//SIGNAL"/>
        <xsl:text>&#9;&#9;} catch (Exception e) { } &#xA;</xsl:text>
        <xsl:text>&#9;}&#xA;</xsl:text>        
        <xsl:text>}&#xA;</xsl:text>
        <!-- ### END generate method for creating design-->
    </xsl:template>
   
    <!-- #### BEGIN generation of components -->
    <xsl:template match="COMPONENT">
        <xsl:variable name="nome"><xsl:value-of select="@name"/></xsl:variable>
        <xsl:text>&#9;&#9;&#9;Node </xsl:text>
        <xsl:value-of select="$nome"/>
        <xsl:text> = new Node("</xsl:text>
        <xsl:value-of select="$nome"/>
        <xsl:text>", "</xsl:text>
        <xsl:value-of select="@unit"/>
        <xsl:text>", "</xsl:text>
        <xsl:value-of select="@operation"/>
        <xsl:text>");&#xA;</xsl:text>
        <xsl:text>&#9;&#9;&#9;graph.addVertex(</xsl:text>
        <xsl:value-of select="$nome"/>
        <xsl:text>);</xsl:text>        
        <xsl:apply-templates select="./PORT"/>
        <xsl:text>&#xA;&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="PORT">
        <xsl:text>&#xA;&#9;&#9;&#9;</xsl:text>
        <xsl:value-of select="$nome"/>
        <xsl:text>.setPortValue("</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>", "</xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>");</xsl:text>
    </xsl:template>
    <!-- #### END generation of components -->
    
    <!-- #### BEGIN generation of connections -->
    <xsl:template match="SIGNAL">
        <xsl:text>&#9;&#9;&#9;graph.addEdge(</xsl:text>
        <xsl:apply-templates select="./SOURCE"/>
        <xsl:text>, </xsl:text>
        <xsl:apply-templates select="./SINK"/>
        <xsl:text>);&#xA;</xsl:text>
    </xsl:template>    
    
    <xsl:template match="SOURCE">
        <xsl:value-of select="@name"/>
    </xsl:template>
    
    <xsl:template match="SINK">
        <xsl:value-of select="@name"/>
    </xsl:template>
    <!-- #### END generation of connections -->
</xsl:stylesheet>
