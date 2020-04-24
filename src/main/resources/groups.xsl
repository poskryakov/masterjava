<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" omit-xml-declaration="yes" indent="no" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <!-- Receives the name of the group being rendered. -->
    <xsl:param name="project_name" />

    <xsl:template match="/">
        <table>
            <tr><th>Group name</th><th>Group type</th></tr>
            <xsl:for-each select="/*[name()='Payload']/*[name()='Project'][@name=$project_name]/*[name()='Group']">
                <tr><td><xsl:value-of select="@name"/></td><td><xsl:value-of select="@type"/></td></tr>
            </xsl:for-each>
        </table>
    </xsl:template>

    <!-- Stop preset XSLT template from printing everything -->
    <xsl:template match="text()"/>

</xsl:stylesheet>