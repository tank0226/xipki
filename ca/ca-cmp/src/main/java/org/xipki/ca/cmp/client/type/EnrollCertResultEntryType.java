/*
 * Copyright 2014 xipki.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 */

package org.xipki.ca.cmp.client.type;

import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.PKIStatus;

public class EnrollCertResultEntryType extends ResultEntryType
{
    private final CMPCertificate cert;
    private final int status;

    public EnrollCertResultEntryType(String id, CMPCertificate cert)
    {
        this(id, cert, PKIStatus.GRANTED);
    }

    public EnrollCertResultEntryType(String id, CMPCertificate cert, int status)
    {
        super(id);
        this.cert = cert;
        this.status = status;
    }

    public CMPCertificate getCert() {
        return cert;
    }

    public int getStatus() {
        return status;
    }
}
