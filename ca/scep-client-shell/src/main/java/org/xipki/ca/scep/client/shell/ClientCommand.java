/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2014 - 2015 Lijun Liao
 * Author: Lijun Liao
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * THE AUTHOR LIJUN LIAO. LIJUN LIAO DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the XiPKI software without
 * disclosing the source code of your own applications.
 *
 * For more information, please contact Lijun Liao at this
 * address: lijun.liao@gmail.com
 */

package org.xipki.ca.scep.client.shell;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.karaf.shell.commands.Option;
import org.xipki.console.karaf.XipkiOsgiCommandSupport;
import org.xipki.scep4j.client.CACertValidator;
import org.xipki.scep4j.client.CAIdentifier;
import org.xipki.scep4j.client.PreprovisionedCACertValidator;
import org.xipki.scep4j.client.ScepClient;
import org.xipki.security.api.util.X509Util;

/**
 * @author Lijun Liao
 */

public abstract class ClientCommand extends XipkiOsgiCommandSupport
{
    @Option(name = "--url",
            required = true,
            description = "URL of the SCEP server\n"
                    + "(required)")
    private String url;

    @Option(name = "--ca-id",
            description = "CA identifier")
    private String caId;

    @Option(name = "--ca-cert",
            required = true,
            description = "CA certificate\n"
                    + "(required)")
    private String caCertFile;

    @Option(name = "--p12",
            required = true,
            description = "PKCS#12 keystore file\n"
                    + "(required)")
    private String p12File;

    @Option(name = "--password",
            description = "password of the PKCS#12 file")
    private String password;

    private ScepClient scepClient;
    private PrivateKey identityKey;
    private X509Certificate identityCert;

    protected ScepClient getScepClient()
    throws CertificateException, IOException
    {
        if(scepClient == null)
        {
            X509Certificate caCert = X509Util.parseCert(caCertFile);
            CAIdentifier _caId = new CAIdentifier(url, caId);
            CACertValidator cACertValidator = new PreprovisionedCACertValidator(caCert);
            scepClient = new ScepClient(_caId, cACertValidator);
        }
        return scepClient;
    }

    protected PrivateKey getIdentityKey()
    throws Exception
    {
        if(identityKey == null)
        {
            readIdentity();
        }
        return identityKey;
    }

    protected X509Certificate getIdentityCert()
    throws Exception
    {
        if(identityCert == null)
        {
            readIdentity();
        }

        return identityCert;
    }

    private void readIdentity()
    throws Exception
    {
        char[] pwd = readPasswordIfNotSet(password);

        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(new FileInputStream(p12File), pwd);

        String keyname = null;
        Enumeration<String> aliases = ks.aliases();
        while(aliases.hasMoreElements())
        {
            String alias = aliases.nextElement();
            if(ks.isKeyEntry(alias))
            {
                keyname = alias;
                break;
            }
        }

        if(keyname == null)
        {
            throw new Exception("no key entry is contained in the keystore");
        }

        this.identityKey = (PrivateKey) ks.getKey(keyname, pwd);
        this.identityCert = (X509Certificate) ks.getCertificate(keyname);
    }

}