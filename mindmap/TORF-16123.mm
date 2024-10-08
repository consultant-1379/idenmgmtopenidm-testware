<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1394046491668" ID="ID_591768602" MODIFIED="1400871800826" TEXT="TORF-16119">
<node CREATED="1394646430031" ID="ID_245845096" MODIFIED="1394719367628" POSITION="right" TEXT="Functional Tests">
<node CREATED="1394646442891" ID="ID_1788368404" MODIFIED="1403707832744" TEXT="Assign a user to a role">
<node CREATED="1394646652587" ID="ID_983522704" MODIFIED="1394646662070" TEXT="COMPONENT: Security"/>
<node CREATED="1394646663721" ID="ID_1676536149" MODIFIED="1403707826273" TEXT="DESCRIPTION: Assign a user to a role"/>
<node CREATED="1394646685634" ID="ID_452900514" MODIFIED="1394646691912" TEXT="PRIORITY: HIGH"/>
<node CREATED="1394646698138" ID="ID_1639444257" MODIFIED="1394646704743" TEXT="GROUP: Function"/>
<node CREATED="1394646715349" ID="ID_549234572" MODIFIED="1394646746210" TEXT="PRE: SC node has been installed with LITP and TOR security OpenIDM Package"/>
<node CREATED="1394646748004" ID="ID_101620305" MODIFIED="1403707848943" TEXT="EXECUTE: Run curl command to assign a user to a role">
<node CREATED="1394646783757" ID="ID_968973625" MODIFIED="1403707861237" TEXT="VERIFY: The user is assigned to the role"/>
</node>
<node CREATED="1394646807871" ID="ID_1233021180" MODIFIED="1394646812099" TEXT="VUSERS: 1"/>
<node CREATED="1394646814871" ID="ID_331831465" MODIFIED="1394646819816" TEXT="CONTEXT: CLI"/>
</node>
<node CREATED="1394646442891" ID="ID_1871098609" MODIFIED="1403707878312" TEXT="Un-assign a user from a role">
<node CREATED="1394646652587" ID="ID_359160550" MODIFIED="1394646662070" TEXT="COMPONENT: Security"/>
<node CREATED="1394646663721" ID="ID_1345016339" MODIFIED="1403707894348" TEXT="DESCRIPTION: Un-assign a user from a role"/>
<node CREATED="1394646685634" ID="ID_1193448682" MODIFIED="1394646691912" TEXT="PRIORITY: HIGH"/>
<node CREATED="1394646698138" ID="ID_922371777" MODIFIED="1394646704743" TEXT="GROUP: Function"/>
<node CREATED="1394646715349" ID="ID_1815605123" MODIFIED="1394646746210" TEXT="PRE: SC node has been installed with LITP and TOR security OpenIDM Package"/>
<node CREATED="1394646748004" ID="ID_1336912862" MODIFIED="1403707910125" TEXT="EXECUTE: Run curl command to un-assign a user from a role">
<node CREATED="1394646783757" ID="ID_1071864905" MODIFIED="1403707920389" TEXT="VERIFY: The user is un-assigned from the role"/>
</node>
<node CREATED="1394646807871" ID="ID_297043014" MODIFIED="1394646812099" TEXT="VUSERS: 1"/>
<node CREATED="1394646814871" ID="ID_904024114" MODIFIED="1394646819816" TEXT="CONTEXT: CLI"/>
</node>
<node CREATED="1394646442891" ID="ID_1036390064" MODIFIED="1403707956293" TEXT="Role cannot be deleted">
<node CREATED="1394646652587" ID="ID_754238830" MODIFIED="1394646662070" TEXT="COMPONENT: Security"/>
<node CREATED="1394646663721" ID="ID_1833044106" MODIFIED="1403707964758" TEXT="DESCRIPTION: Role cannot be deleted"/>
<node CREATED="1394646685634" ID="ID_1249347572" MODIFIED="1394646691912" TEXT="PRIORITY: HIGH"/>
<node CREATED="1394646698138" ID="ID_1232027149" MODIFIED="1394646704743" TEXT="GROUP: Function"/>
<node CREATED="1394646715349" ID="ID_51418877" MODIFIED="1394646746210" TEXT="PRE: SC node has been installed with LITP and TOR security OpenIDM Package"/>
<node CREATED="1394646748004" ID="ID_1722107215" MODIFIED="1400872000377" TEXT="EXECUTE: Run curl command to delete a role">
<node CREATED="1394646783757" ID="ID_1195848160" MODIFIED="1403707945442" TEXT="VERIFY: The delete was rejected."/>
</node>
<node CREATED="1394646807871" ID="ID_618703120" MODIFIED="1394646812099" TEXT="VUSERS: 1"/>
<node CREATED="1394646814871" ID="ID_1061222949" MODIFIED="1394646819816" TEXT="CONTEXT: CLI"/>
</node>
</node>
</node>
</map>
