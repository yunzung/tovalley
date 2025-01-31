import { view } from '@store/chat/chatViewSlice'
import { setNotificationView } from '@store/notification/notificationViewSlice'
import { useEffect, useState } from 'react'
import { useDispatch } from 'react-redux'
import styled from 'styled-components'

const Wrapper = styled.div<{ appear: string; size: number }>`
  transform: ${({ appear, size }) =>
    appear === 'start' ? `translateX(-${size}px)` : `translateX(${size}px)`};
  transition: ${({ appear }) => (appear === 'start' ? `all 0.5s` : `all 1s`)};
`

interface DrawerProps {
  children: React.ReactNode
  classNames: { container: string; wrapper: string }
  size: number
  isView: boolean
  isAlarm?: boolean
}

const Drawer = ({
  children,
  classNames,
  size,
  isView,
  isAlarm,
}: DrawerProps) => {
  const [bgForeground, setBgForeground] = useState(false)
  const [appear, setAppear] = useState('')
  const dispatch = useDispatch()

  useEffect(() => {
    if (isView) {
      document.body.style.cssText = `
        top: -${window.scrollY}px;
        overflow-y: scroll;
        width: 100%;`
      return () => {
        const scrollY = document.body.style.top
        document.body.style.cssText = ''
        window.scrollTo(0, parseInt(scrollY || '0', 10) * -1)
      }
    }
  }, [isView])

  useEffect(() => {
    if (isView) {
      setAppear('start')
      dispatch(isAlarm ? view(false) : setNotificationView(false))
    } else {
      setAppear('end')
    }
  }, [isAlarm, isView, dispatch])

  useEffect(() => {
    if (appear === 'end') {
      const fadeTimer = setTimeout(() => {
        setBgForeground(true)
      }, 500)
      return () => clearTimeout(fadeTimer)
    } else {
      setBgForeground(false)
    }
  }, [appear])

  return (
    <div
      className={classNames.container}
      style={!bgForeground ? { zIndex: 20 } : undefined}
    >
      <Wrapper className={classNames.wrapper} appear={appear} size={size}>
        {children}
      </Wrapper>
    </div>
  )
}

export default Drawer
