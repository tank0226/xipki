/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2014 - 2016 Lijun Liao
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

package org.xipki.pki.ocsp.client.shell;

import java.util.List;

import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.xipki.console.karaf.XipkiCommandSupport;
import org.xipki.console.karaf.completer.FilePathCompleter;
import org.xipki.console.karaf.completer.HashAlgCompleter;
import org.xipki.console.karaf.completer.SigAlgCompleter;
import org.xipki.pki.ocsp.client.api.OCSPRequestor;
import org.xipki.pki.ocsp.client.api.RequestOptions;
import org.xipki.security.api.util.AlgorithmUtil;

/**
 * @author Lijun Liao
 */

public abstract class AbstractOCSPStatusCmd extends XipkiCommandSupport {
    @Option(name = "--issuer", aliases = "-i",
            required = true,
            description = "issuer certificate file\n"
                    + "(required)")
    @Completion(FilePathCompleter.class)
    protected String issuerCertFile;

    @Option(name = "--nonce",
            description = "use nonce")
    protected Boolean usenonce = Boolean.FALSE;

    @Option(name = "--nonce-len",
            description = "nonce length in octects")
    protected Integer nonceLen;

    @Option(name = "--hash",
            description = "hash algorithm name")
    @Completion(HashAlgCompleter.class)
    protected String hashAlgo = "SHA256";

    @Option(name = "--sig-alg",
            multiValued = true,
            description = "comma-separated preferred signature algorithms\n"
                    + "(multi-valued)")
    @Completion(SigAlgCompleter.class)
    protected List<String> prefSigAlgs;

    @Option(name = "--http-get",
            description = "use HTTP GET for small request")
    protected Boolean useHttpGetForSmallRequest = Boolean.FALSE;

    @Option(name = "--sign",
            description = "sign request")
    protected Boolean signRequest = Boolean.FALSE;

    @Reference
    protected OCSPRequestor requestor;

    protected RequestOptions getRequestOptions()
    throws Exception {
        ASN1ObjectIdentifier hashAlgoOid = AlgorithmUtil.getHashAlg(hashAlgo);
        RequestOptions options = new RequestOptions();
        options.setUseNonce(usenonce.booleanValue());
        if (nonceLen != null) {
            options.setNonceLen(nonceLen);
        }
        options.setHashAlgorithmId(hashAlgoOid);
        options.setSignRequest(signRequest.booleanValue());
        options.setUseHttpGetForRequest(useHttpGetForSmallRequest.booleanValue());

        if (isNotEmpty(prefSigAlgs)) {
            options.setPreferredSignatureAlgorithms2(prefSigAlgs);
        }
        return options;
    }

}
