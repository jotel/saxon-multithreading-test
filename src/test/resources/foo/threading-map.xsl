<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        exclude-result-prefixes="xs"
        version="3.0">

    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="lookup" as="map(xs:string,xs:string)">
        <xsl:map>
            <xsl:source-document href="ctx:vars/myvar" streamable="yes">
                <xsl:for-each select="/root/foo">
                    <xsl:map-entry key="string(@id)" select="string(text())"/>
                </xsl:for-each>
            </xsl:source-document>
        </xsl:map>
    </xsl:variable>

    <xsl:template match="/">
        <foo>
            <xsl:for-each select="root/row">
                <data>
                    <xsl:value-of select="$lookup( @id )"/>
                </data>
            </xsl:for-each>
        </foo>
    </xsl:template>
</xsl:stylesheet>