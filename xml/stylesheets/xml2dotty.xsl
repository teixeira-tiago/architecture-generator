<?xml version="1.0" encoding="UTF-8"?>
<!-- XML to Java XSLT Transformer 
    February, 2004
    University of Algarve, Portugal
--> 

<!-- TAB  char unicode = 9-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" omit-xml-declaration="no" indent="yes"/>
    <xsl:template match="/">
        <xsl:text>digraph design {&#xA;</xsl:text>
        <xsl:apply-templates select="//COMPONENT"/>
        <xsl:apply-templates select="//SIGNAL"/>
        <xsl:text>&#xA;}&#xA;</xsl:text>
    </xsl:template>
    <!-- #### BEGIN generation of components -->
    <xsl:template match="COMPONENT">
        <xsl:text>&#xA;&#9;"</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>" [shape=record, label="{{&lt;in0&gt;A</xsl:text>
        <xsl:apply-templates select="./PORT" mode="A"/>
        <xsl:text>|&lt;in1&gt;B</xsl:text>
        <xsl:apply-templates select="./PORT" mode="B"/>
        <xsl:text>}|</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>\n</xsl:text>
        <xsl:value-of select="@operation"/>
        <xsl:text>|{&lt;out0&gt;Y|&lt;out1&gt;Z}}"];&#xA;</xsl:text>
        <!-- <xsl:apply-templates select="./PORT"/> -->
    </xsl:template>
    <!-- #### END generation of components -->
    <!-- #### BEGIN generation of connections -->
    <xsl:template match="SIGNAL">
        <xsl:text>&#9;"</xsl:text>
        <xsl:apply-templates select="./SOURCE"/>
        <xsl:text> -&gt; "</xsl:text>
        <xsl:apply-templates select="./SINK"/>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    <!-- #### END generation of components -->
    <xsl:template match="SOURCE">
        <xsl:value-of select="@name"/>
        <xsl:text>":</xsl:text>
        <xsl:value-of select="@port"/>
    </xsl:template>
    <xsl:template match="SINK">
        <xsl:value-of select="@name"/>
        <xsl:text>":</xsl:text>
        <xsl:value-of select="@port"/>
    </xsl:template>
    <xsl:template match="PORT" mode="A">
        <!-- BEGIN for const values -->
        <xsl:if test="@value and @name='A'">
            <xsl:text>=</xsl:text>
            <xsl:value-of select="@value"/>
        </xsl:if>
        <!-- END for const values -->
    </xsl:template>
    <xsl:template match="PORT" mode="B">
        <!-- BEGIN for const values -->
        <xsl:if test="@value and @name='B'">
            <xsl:text>=</xsl:text>
            <xsl:value-of select="@value"/>
        </xsl:if>
        <!-- END for const values -->
    </xsl:template>
</xsl:stylesheet>
