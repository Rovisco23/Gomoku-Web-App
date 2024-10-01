import {useEffect, useState,} from 'react'

export type fetchRsp = {
    response: any,
    error: any
}

export function useFetch(uri: string, method: string, headers: object = null, bodyRequest: any = null): fetchRsp {
    console.log("inside useFetch")
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)

    useEffect(() => {
        let canceled = false
        async function doFetch() {
            try {
                const rsp = await fetch(uri, {
                    method: method,
                    headers: {
                        ...headers,
                        'Content-Type': 'application/json'
                    },
                    body: bodyRequest
                })
                console.log("finished fetch")
                if (!rsp.ok) {
                    const contentType = rsp.headers.get("content-type");
                    if (contentType && contentType.indexOf("application/problem+json") !== -1) {
                        const bodyError = await rsp.text();
                        setError(bodyError);
                        return;
                    }
                }
                const bodyResponse = await rsp.json()
                if (canceled) return
                setContent(bodyResponse)
            } catch (err) {
                console.log("Error in doFetch", err)
            }
        }
        if (!content && !error) doFetch()
        return () => {
            console.log("CleanUp")
            canceled = true
        }
    }, [uri, method, headers, bodyRequest])

    return {response: content, error: error}
}