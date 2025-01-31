import { AccidentCountDto } from 'types/main'
import styles from './AccidentTable.module.scss'
import { Accidents } from 'types/valley'
import React from 'react'

const AccidentTable = React.memo(
  ({ accident }: { accident: AccidentCountDto | Accidents }) => {
    return (
      <div className={styles.graph}>
        <div className={styles.graphTitle}>
          <span>사망</span>
          <span>실종</span>
          <span>부상</span>
        </div>
        <div className={styles.graphContent}>
          <span>{accident.totalDeathCnt}</span>
          <span>{accident.totalDisappearanceCnt}</span>
          <span>{accident.totalInjuryCnt}</span>
        </div>
      </div>
    )
  },
  (prevProps, nextProps) =>
    JSON.stringify(prevProps.accident) === JSON.stringify(nextProps.accident)
)

export default AccidentTable
